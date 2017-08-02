# coding=UTF-8
import unittest
import time
import datetime
import random
import sys
from unittest.case import _AssertRaisesContext

from selenium import webdriver
from selenium.webdriver.support.ui import Select
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.action_chains import ActionChains

import subprocess
import os
import psycopg2
import requests
import urllib.request
from os.path import expanduser
import csv
import codecs
import xmlrunner
# Import parameters from parameter file
from UnionVMSparameters import *


def externalError(process):
   print("Process '%s' returned code %s" % (process.args, process.returncode))
   #print("Run time: %s " % (time.time() - startTime))
   sys.exit(process.returncode)


def runSubProcess(command, shell, stdout=None):
   process = subprocess.Popen(command, shell=shell, stdout=stdout)
   process.wait()
   if process.returncode != 0:
       externalError(process)
   return process


def populateIridiumImarsatCData():
    try:
        conn = psycopg2.connect(connectToDatabaseString)
        print("setup precondition populateIridiumImarsatCData")
        cur = conn.cursor()

        cur.execute("""SELECT * from mobterm.plugin where name='siriusone' or name='twostage' """)

        if len(cur.fetchall()) == 0:
            print("Insert data in mobterm.plugin")
            cur.execute("""INSERT INTO mobterm.plugin VALUES (%s, %s, %s, %s, %s, %s, %s, %s);""",
                        (1050, 'siriusone', 'eu.europa.ec.fisheries.uvms.plugins.iridium.siriusone', 'IRIDIUM', False,
                         'siriusone', datetime.datetime.utcnow(), 'UVMS'))
            cur.execute("""INSERT INTO mobterm.plugin VALUES (%s, %s, %s, %s, %s, %s, %s, %s);""",
                        (1056, 'twostage', 'eu.europa.ec.fisheries.uvms.plugins.inmarsat', 'INMARSAT_C', False,
                         'twostage', datetime.datetime.utcnow(), 'UVMS'))
            conn.commit()
        else:
            cur.execute("""update mobterm.plugin set inactive=false where name='siriusone' or name='twostage' """)
            conn.commit()
            print("Update already exist in mobterm.plugin")

        # Add rows to mobterm.plugin_capability
        cur.execute("""SELECT * from mobterm.plugin_capability where capability='SAMPLING' """)
        if len(cur.fetchall()) == 0:
            print("Insert data in mobterm.plugin_capability")
            cur.execute("""INSERT INTO mobterm.plugin_capability VALUES (%s, %s, %s, %s, %s, %s);""",
                        (1051, 1050, 'CONFIGURABLE', 'TRUE', datetime.datetime.utcnow(), 'UVMS'))
            cur.execute("""INSERT INTO mobterm.plugin_capability VALUES (%s, %s, %s, %s, %s, %s);""",
                        (1052, 1050, 'SAMPLING', 'TRUE', datetime.datetime.utcnow(), 'UVMS'))
            cur.execute("""INSERT INTO mobterm.plugin_capability VALUES (%s, %s, %s, %s, %s, %s);""",
                        (1053, 1050, 'ONLY_SINGLE_OCEAN', 'TRUE', datetime.datetime.utcnow(), 'UVMS'))
            cur.execute("""INSERT INTO mobterm.plugin_capability VALUES (%s, %s, %s, %s, %s, %s);""",
                        (1054, 1050, 'MULTIPLE_OCEAN', 'FALSE', datetime.datetime.utcnow(), 'UVMS'))
            cur.execute("""INSERT INTO mobterm.plugin_capability VALUES (%s, %s, %s, %s, %s, %s);""",
                        (1055, 1050, 'POLLABLE', 'TRUE', datetime.datetime.utcnow(), 'UVMS'))
            cur.execute("""INSERT INTO mobterm.plugin_capability VALUES (%s, %s, %s, %s, %s, %s);""",
                        (1057, 1056, 'ONLY_SINGLE_OCEAN', 'TRUE', datetime.datetime.utcnow(), 'UVMS'))
            cur.execute("""INSERT INTO mobterm.plugin_capability VALUES (%s, %s, %s, %s, %s, %s);""",
                        (1058, 1056, 'POLLABLE', 'TRUE', datetime.datetime.utcnow(), 'UVMS'))
            cur.execute("""INSERT INTO mobterm.plugin_capability VALUES (%s, %s, %s, %s, %s, %s);""",
                        (1059, 1056, 'CONFIGURABLE', 'TRUE', datetime.datetime.utcnow(), 'UVMS'))
            cur.execute("""INSERT INTO mobterm.plugin_capability VALUES (%s, %s, %s, %s, %s, %s);""",
                        (1060, 1056, 'SAMPLING', 'TRUE', datetime.datetime.utcnow(), 'UVMS'))
            cur.execute("""INSERT INTO mobterm.plugin_capability VALUES (%s, %s, %s, %s, %s, %s);""",
                        (1061, 1056, 'MULTIPLE_OCEAN', 'TRUE', datetime.datetime.utcnow(), 'UVMS'))

            conn.commit()
        else:
            print("Data already exist in mobterm.plugin_capability")

    except:
        print("I am unable to connect to the database")
    cur.close()
    conn.close()


def populateSanityRuleData():
    try:
        conn = psycopg2.connect(connectToDatabaseString)
        print("Yeeahh I am in!!!")
        cur = conn.cursor()
        cur.execute("""SELECT * from rules.sanityrule""")
        rows = cur.fetchall()
        print("\nPrint out of Database " + dbServerName + " (Before):\n")
        for row in rows:
            print(row[0:])

        cur.execute("""UPDATE rules.sanityrule SET sanityrule_expression = 'mobileTerminalConnectId == null && pluginType != "NAF"' WHERE sanityrule_expression = 'mobileTerminalConnectId == null';""")

        cur.execute("""SELECT * from rules.sanityrule""")
        print("\nPrint out of Database " + dbServerName + " (After):\n")
        rows = cur.fetchall()
        for row in rows:
            print(row[0:])
        conn.commit()


    except:
        print("I am unable to connect to the database")
    cur.close()
    conn.close()


def startup_browser_and_login_to_unionVMS(cls,userId,password,userContext):
    cls.driver = webdriver.Chrome()
    cls.driver.maximize_window()
    cls.driver.get(httpUnionVMSurlString)

    # if Hav och vatten proxy page is presented, then autologin
    try:
        if cls.driver.find_element_by_xpath("/html/head/title"):
            cls.driver.switch_to.frame("content")
            WebDriverWait(cls.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR,"img[alt=\"Automatisk inloggning\"]"))).click()
    except:
        pass


    # if Pop-up windows exists then click cancel
    try:
        if cls.driver.find_element_by_xpath("/html/body/div[5]/div/div/div/form"):
            WebDriverWait(cls.driver, browserTimeout).until(EC.element_to_be_clickable((By.XPATH,"/html/body/div[5]/div/div/div/form/div[3]/button[2]"))).click()
    except:
        pass

    WebDriverWait(cls.driver, browserTimeout).until(EC.presence_of_element_located((By.ID, 'userId'))).send_keys(userId)
    WebDriverWait(cls.driver, browserTimeout).until(EC.presence_of_element_located((By.ID, 'password'))).send_keys(password)
    WebDriverWait(cls.driver, browserTimeout).until(EC.element_to_be_clickable((By.XPATH, "//*[@id='content']/div[1]/div[3]/div/div[2]/div[3]/div[2]/form/div[3]/div/button"))).click()
    time.sleep(browserWaitAfterClick)

    WebDriverWait(cls.driver, browserTimeout).until(EC.element_to_be_clickable((By.PARTIAL_LINK_TEXT, "AdminAll"))).click()
    time.sleep(browserWaitAfterClick)
    time.sleep(browserWaitAfterClick)

def shutdown_browser(cls):
    cls.driver.quit()


def create_one_new_asset_from_gui(self,ircsValue,vesselName,externalMarkingValue,cfrValue,imoValue,homeportValue,mmsiValue,lengthValue,grossTonnageValue,powerValue,producernameValue,producercodeValue,contactNameValue,contactEmailValue,contactPhoneNumberValue):

    # Click on asset tab
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"uvms-header-menu-item-assets"))).click()

    # Click on new Asset button
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"asset-btn-create"))).click()

    # Select F.S value
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"asset-input-countryCode"))).click()
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"asset-input-countryCode-item-2"))).click()
    # Enter IRCS value
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-ircs"))).send_keys(ircsValue)
    # Enter Name value
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-name"))).send_keys(vesselName)
    # Enter External Marking Value
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-externalMarking"))).send_keys(externalMarkingValue)
    # Enter CFR Value
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-cfr"))).send_keys(cfrValue)
    # Enter IMO Value
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-imo"))).send_keys(imoValue)
    # Enter HomePort Value
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-homeport"))).send_keys(homeportValue)
    # Select Gear Type value
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"asset-input-gearType"))).click()
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"asset-input-gearType-item-0"))).click()
    # Enter MMSI Value
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-mmsi"))).send_keys(mmsiValue)
    # Select License Type value
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"asset-input-licenseType"))).click()

    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"asset-input-licenseType-item-0"))).click()
    # Length Value
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-lengthValue"))).send_keys(lengthValue)
    # Gross Tonnage Value
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-grossTonnage"))).send_keys(grossTonnageValue)
    # Main Power Value
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-power"))).send_keys(powerValue)
    # Main Producer Name Value
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-producername"))).send_keys(producernameValue)
    # Main Producer Code Value
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-producercode"))).send_keys(producercodeValue)
    # Click on the Contacts tab
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.XPATH,"//*[@id='CONTACTS']/span"))).click()

    # Main Contact Name Value
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-contact-name-0"))).send_keys(contactNameValue)
    # Main E-mail Value
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-contact-email-0"))).send_keys(contactEmailValue)
    # Main Contact Number Value
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-contact-number-0"))).send_keys(contactPhoneNumberValue)
    # Click on Save Asset button
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"menu-bar-save"))).click()
    time.sleep(browserWaitAfterClick)
    # Leave new asset view
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"menu-bar-cancel"))).click()



def create_one_new_mobile_terminal_from_gui(self, serialNoValue, transceiverType, softwareVersion, antennaVersion, satelliteNumber, dnidNumber, memberIdnumber, installedByName, expectedFrequencyHours, gracePeriodFrequencyHours, inPortFrequencyHours):
    # Startup browser and login
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"uvms-header-menu-item-communication"))).click()

    # Click on new terminal button
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"mt-btn-create"))).click()

    # Select Transponder system
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"mt-0-typeAndPlugin"))).click()

    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.LINK_TEXT,"Inmarsat-C : twostage"))).click()

    # Enter serial number
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-serialNumber"))).send_keys(serialNoValue)
    # Enter Transceiver type
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-tranciverType"))).send_keys(transceiverType)
    # Enter Software Version
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-softwareVersion"))).send_keys(softwareVersion)
    # Enter Antenna
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-antenna"))).send_keys(antennaVersion)
    # Enter Satellite Number
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-satelliteNumber"))).send_keys(satelliteNumber)
    # Enter DNID Number
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.NAME,"dnid"))).send_keys(dnidNumber)
    # Enter Member Number
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.NAME,"memberId"))).send_keys(memberIdnumber)
    # Enter Installed by
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-channel-0-installedBy"))).send_keys(installedByName)
    # Expected frequency
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-channel-0-frequencyExpected"))).send_keys(expectedFrequencyHours)
    # Grace period
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-channel-0-frequencyGrace"))).send_keys(gracePeriodFrequencyHours)
    # In port
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-channel-0-frequencyPort"))).send_keys(inPortFrequencyHours)

    # Activate Mobile Terminal button
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"mt-0-activation"))).click()

    # Click on save button
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"menu-bar-save"))).click()

    # Leave new asset view
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"menu-bar-cancel"))).click()



def create_one_new_mobile_terminal_via_asset_tab(self, mobileTerminalNumber, vesselNumber):
    # Startup browser and login
    startup_browser_and_login_to_unionVMS(self)

    # Click on asset tab
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"uvms-header-menu-item-assets"))).click()

    # Search for created asset
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-simple-search"))).clear()
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-simple-search"))).send_keys(vesselName[vesselNumber])

    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"asset-btn-simple-search"))).click()

    # Click on details button
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"asset-toggle-form"))).click()

    # Click on add new terminal button
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"menu-bar-vessel-add-terminal"))).click()

    # Select Transponder system
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"mt-0-typeAndPlugin"))).click()

    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.LINK_TEXT,"Inmarsat-C : twostage"))).click()

    # Enter serial number
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-serialNumber"))).send_keys(serialNoValue[mobileTerminalNumber])
    # Enter Transceiver type
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-tranciverType"))).send_keys(transceiverType[mobileTerminalNumber])
    # Enter Software Version
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-softwareVersion"))).send_keys(softwareVersion)
    # Enter Antenna
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-antenna"))).send_keys(antennaVersion)
    # Enter Satellite Number
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-satelliteNumber"))).send_keys(satelliteNumber[mobileTerminalNumber])
    # Enter DNID Number
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.NAME,"dnid"))).send_keys(dnidNumber[mobileTerminalNumber])
    # Enter Member Number
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.NAME,"memberId"))).send_keys(memberIdnumber[mobileTerminalNumber])
    # Enter Installed by
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-channel-0-installedBy"))).send_keys(installedByName)
    # Expected frequency
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-channel-0-frequencyExpected"))).send_keys(expectedFrequencyHours)
    # Grace period
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-channel-0-frequencyGrace"))).send_keys(gracePeriodFrequencyHours)
    # In port
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-channel-0-frequencyPort"))).send_keys(inPortFrequencyHours)
    # Activate Mobile Terminal button
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"mt-0-activation"))).click()

    # Click on save button
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.XPATH,"//*[@id='menu-bar-update']"))).click()

    # Leave new asset view
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"menu-bar-cancel"))).click()

    # Shutdown browser
    shutdown_browser(self)


def check_new_asset_exists(self,countryValue,gearTypeValue,licenseTypeValue, ircsValue,vesselName,externalMarkingValue,cfrValue,imoValue,homeportValue,mmsiValue,lengthValue,grossTonnageValue,powerValue,producernameValue,producercodeValue,contactNameValue,contactEmailValue,contactPhoneNumberValue):
    # Startup browser and login
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"uvms-header-menu-item-assets"))).click()

    # Search for the new created asset in the asset list
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-simple-search"))).send_keys(vesselName)
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"asset-btn-simple-search"))).click()

    # Check that the new asset exists in the list.
    self.assertEqual(vesselName, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"td[title=\"" + vesselName + "\"]"))).text)

    # Click on details button for new asset
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"asset-toggle-form"))).click()

    # Check that the F.S value is correct.
    self.assertEqual(countryValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-countryCode"))).text)
    # Check that the IRCS value is correct
    self.assertEqual(ircsValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-ircs"))).get_attribute("value"))
    # Check that the Name value is correct
    self.assertEqual(vesselName, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-name"))).get_attribute("value"))
    # Check that External Marking Value is correct
    self.assertEqual(externalMarkingValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-externalMarking"))).get_attribute("value"))
    # Check that the CFR value is correct
    self.assertEqual(cfrValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-cfr"))).get_attribute("value"))
    # Check that the IMO value is correct
    self.assertEqual(imoValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-imo"))).get_attribute("value"))
    # Check that the HomePort value is correct
    self.assertEqual(homeportValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-homeport"))).get_attribute("value"))
    # Check that the Gear Type value is correct.
    self.assertEqual(gearTypeValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-gearType"))).text)
    # Check that the MMSI value is correct
    self.assertEqual(mmsiValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-mmsi"))).get_attribute("value"))
    # Check that the License Type value is correct.
    self.assertEqual(licenseTypeValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-licenseType"))).text)
    # Check that the Length Type value is correct.
    self.assertEqual(lengthValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-lengthValue"))).get_attribute("value"))
    # Check that the Gross Tonnage value is correct.
    self.assertEqual(grossTonnageValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-grossTonnage"))).get_attribute("value"))
    # Check that the Power value is correct.
    self.assertEqual(powerValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-power"))).get_attribute("value"))
    # Check that the Producer Name value is correct.
    #
    # Needs to be updated according to asset database
    #
    #
    # self.assertEqual("Mikael", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-producername"))).get_attribute("value"))
    # Check that the Producer Code value is correct.
    self.assertEqual(producercodeValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-producercode"))).get_attribute("value"))
    # Click on the Contacts tab
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.XPATH,"//*[@id='CONTACTS']/span"))).click()

    # Check that the Contact Name value is correct.
    self.assertEqual(contactNameValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-contact-name-0"))).get_attribute("value"))
    # Check that the E-mail value is correct.
    self.assertEqual(contactEmailValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-contact-email-0"))).get_attribute("value"))
    # Check that the E-mail value is correct.
    self.assertEqual(contactPhoneNumberValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"asset-input-contact-number-0"))).get_attribute("value"))

    # Shutdown browser

def check_new_mobile_terminal_exists(self, serialNoValue, memberIdnumber, dnidNumber, transceiverType, softwareVersion, satelliteNumber, antennaVersion, installedByName):
    # Select Mobile Terminal tab
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"uvms-header-menu-item-communication"))).click()
    time.sleep(browserWaitAfterClick)

    # Enter Serial Number in
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"(//input[@type='text'])[7]"))).send_keys(serialNoValue)
    # Click in search button
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.XPATH,"//button[@type='submit']"))).click()
    time.sleep(browserWaitAfterClick)

    # Check Serial Number in the list
    self.assertEqual(serialNoValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"//div[@id='content']/div/div[3]/div[2]/div/div/div/div/div[3]/div/div/div/div/span/table/tbody/tr/td[3]"))).text)
    # Check Member Number in the list
    self.assertEqual(memberIdnumber, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"//div[@id='content']/div/div[3]/div[2]/div/div/div/div/div[3]/div/div/div/div/span/table/tbody/tr/td[4]"))).text)
    # Check DNID Number in the list
    self.assertEqual(dnidNumber, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"//div[@id='content']/div/div[3]/div[2]/div/div/div/div/div[3]/div/div/div/div/span/table/tbody/tr/td[5]"))).text)
    # Click on details button
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.XPATH,"//div[@id='content']/div/div[3]/div[2]/div/div/div/div/div[3]/div/div/div/div/span/table/tbody/tr/td[10]/button"))).click()
    time.sleep(browserWaitAfterClick)

    # Check Serial Number
    self.assertEqual(serialNoValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-serialNumber"))).get_attribute("value"))
    # Check Transceiver Type
    self.assertEqual(transceiverType, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-tranciverType"))).get_attribute("value"))
    # Check Software Version
    self.assertEqual(softwareVersion, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-softwareVersion"))).get_attribute("value"))
    # Check Satellite Number
    self.assertEqual(satelliteNumber, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-satelliteNumber"))).get_attribute("value"))
    # Check Antenna Version
    self.assertEqual(antennaVersion, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-antenna"))).get_attribute("value"))
    # Check DNID Number
    self.assertEqual(dnidNumber, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.NAME,"dnid"))).get_attribute("value"))
    # Check Member Number
    self.assertEqual(memberIdnumber, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.NAME,"memberId"))).get_attribute("value"))
    # Check Installed by Name
    self.assertEqual(installedByName, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-0-channel-0-installedBy"))).get_attribute("value"))
    # Leave new asset view
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"menu-bar-cancel"))).click()



def link_asset_and_mobile_terminal(self, serialNoValue, ircsValue):
    # Select Mobile Terminal tab
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"uvms-header-menu-item-communication"))).click()

    # Enter Serial Number in field
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-input-search-serialNumber"))).clear()

    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-input-search-serialNumber"))).send_keys(serialNoValue)
    # Click in search button
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"mt-btn-advanced-search"))).click()
    time.sleep(browserWaitAfterClick)

    # Click on details button
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"mt-toggle-form"))).click()

    # Click on Link Asset
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"mt-btn-assign-asset"))).click()

    # Enter Asset Name and clicks on the search button
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"(//input[@type='text'])[23]"))).send_keys(ircsValue)
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.XPATH,"//button[@type='submit']"))).click()
    time.sleep(browserWaitAfterClick)

    # Click on connect button
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR ,"td.textAlignRight > button.btn.btn-primary"))).click()
    # Click on Link button

    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR ,"div.col-md-6.textAlignRight > button.btn.btn-primary"))).click()
    # Enter Reason comment
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.NAME,"comment"))).send_keys("Need to connect this mobile terminal with this asset.")

    # Click on Link button 2
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR ,"div.modal-footer > div.row > div.col-md-12 > button.btn.btn-primary"))).click()
    time.sleep(browserWaitAfterClick)
    # Close page
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"menu-bar-cancel"))).click()



def change_and_check_speed_format(self,unitNumber):
    # Startup browser and login
    startup_browser_and_login_to_unionVMS(self)

    # Select Admin tab
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"uvms-header-menu-item-audit-log"))).click()

    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.LINK_TEXT,"CONFIGURATION"))).click()

    # Click on Global setting subtab under Configuration Tab
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"#globalSettings > span"))).click()

    # Set Speed format to knots
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"(//button[@type='button'])[4]"))).click()

    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.LINK_TEXT,speedUnitTypesInText[unitNumber]))).click()

    # Click on Position Tab to check correct speed unit
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"uvms-header-menu-item-movement"))).click()

    currentSpeedValue = WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"//*[@id='content']/div[1]/div[3]/div[2]/div/div[2]/div/div[4]/div/div/div/div/span/table/tbody/tr[1]/td[11]"))).text
    print("Current: " +  currentSpeedValue + " Short Unit: " + speedUnitTypesShort[unitNumber])
    if currentSpeedValue.find(speedUnitTypesShort[unitNumber]) == -1:
        foundCorrectUnit = False
    else:
        foundCorrectUnit = True
    self.assertTrue(foundCorrectUnit)

    # Shutdown browser
    shutdown_browser(self)



def generate_and_verify_manual_position(self,speedValue,courseValue,ircsValue,cfr,lolaPositionValues,countryValue,externalMarkingValue,vesselName,sourceValue,deltaTimeValue):
    # Select Positions tab
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"uvms-header-menu-item-movement"))).click()

    # Click on New manual report
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.XPATH,"//button[@type='submit']"))).click()
    time.sleep(browserWaitAfterClick)

    # Enter IRCS value
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.NAME,"ircs"))).send_keys(ircsValue)
    time.sleep(browserWaitAfterClick)
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.LINK_TEXT,ircsValue))).click()


    currentUTCValue = datetime.datetime.utcnow()
    earlierPositionTimeValue = currentUTCValue - datetime.timedelta(hours=deltaTimeValue)
    earlierPositionDateTimeValueString = datetime.datetime.strftime(earlierPositionTimeValue, '%Y-%m-%d %H:%M:%S')
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"manual-movement-date-picker"))).clear()
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"manual-movement-date-picker"))).send_keys(earlierPositionDateTimeValueString)

    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.NAME,"name"))).send_keys("ManualPosition" + ircsValue)

    # Enter Position, Speed and Course
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.NAME,"latitude"))).clear()
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.NAME,"latitude"))).send_keys(lolaPositionValues[0][0][0])
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.NAME,"longitude"))).clear()
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.NAME,"longitude"))).send_keys(lolaPositionValues[0][0][1])
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.NAME,"measuredSpeed"))).send_keys(str(speedValue))

    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.NAME,"course"))).send_keys(str(courseValue))
    # Click on Save Button
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.XPATH,"(//button[@type='submit'])[4]"))).click()
    time.sleep(browserWaitAfterClick)

    # Click on Confirm button
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.XPATH,"(//button[@type='submit'])[4]"))).click()
    time.sleep(browserLongWaitAfterClick)
    WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.LINK_TEXT,"MANUAL POSITION REPORTS"))).click()


    # Verifies position data
    self.assertEqual(externalMarkingValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"td[title=\"" + externalMarkingValue + "\"]"))).text)
    self.assertEqual(ircsValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"td[title=\"" + ircsValue + "\"]"))).text)
    #self.assertEqual(vesselName, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.LINK_TEXT,vesselName).text)
    # Bug UVMS-3249 self.assertEqual(earlierPositionDateTimeValueString, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"//*[@id='content']/div[1]/div[3]/div[2]/div/div[2]/div/div[4]/div/div/div/div/span/table/tbody/tr[1]/td[6]"))).text)
    self.assertEqual(lolaPositionValues[0][0][0], WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"td[title=\"" + lolaPositionValues[0][0][0] + "\"]"))).text)
    self.assertEqual(lolaPositionValues[0][0][1], WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"td[title=\"" + lolaPositionValues[0][0][1] + "\"]"))).text)
#     self.assertEqual("%.2f" % speedValue + " kts", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"td[title=\"" + "%.2f" % speedValue + " kts" + "\"]"))).text)
#     self.assertEqual(str(courseValue) + "°", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"td[title=\"" + str(courseValue) + "°" + "\"]"))).text)
#     self.assertEqual(sourceValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"td[title=\"" + sourceValue + "\"]"))).text)

    return earlierPositionDateTimeValueString


def generate_NAF_and_verify_position(self,speedValue,courseValue):
    # Get Current Date and time in UTC
    currentUTCValue = datetime.datetime.utcnow()
    earlierPositionTimeValue = currentUTCValue - datetime.timedelta(hours=deltaTimeValue)
    earlierPositionDateValueString = datetime.datetime.strftime(earlierPositionTimeValue, '%Y%m%d')
    earlierPositionTimeValueString = datetime.datetime.strftime(earlierPositionTimeValue, '%H%M')
    earlierPositionDateTimeValueString = datetime.datetime.strftime(earlierPositionTimeValue, '%Y-%m-%d %H:%M:00')
    # Generate NAF string to send
    nafSource = '//SR//FR/'
    nafSource = nafSource + countryValue
    nafSource = nafSource + "//AD/UVM//TM/POS//RC/"
    nafSource = nafSource + ircsValue[0]
    nafSource = nafSource + "//IR/"
    nafSource = nafSource + cfrValue[0]
    nafSource = nafSource + "//XR/"
    nafSource = nafSource + externalMarkingValue
    nafSource = nafSource + "//LT/"
    nafSource = nafSource + lolaPositionValues[0][0][0]
    nafSource = nafSource + "//LG/"
    nafSource = nafSource + lolaPositionValues[0][0][1]
    nafSource = nafSource + "//SP/"
    nafSource = nafSource + str(speedValue * 10)
    nafSource = nafSource + "//CO/"
    nafSource = nafSource + str(courseValue)
    nafSource = nafSource + "//DA/"
    nafSource = nafSource + earlierPositionDateValueString
    nafSource = nafSource + "//TI/"
    nafSource = nafSource + earlierPositionTimeValueString
    nafSource = nafSource + "//NA/"
    nafSource = nafSource + vesselName[0]
    nafSource = nafSource + "//FS/"
    nafSource = nafSource + countryValue
    nafSource = nafSource + "//ER//"
    nafSourceURLcoded = urllib.parse.quote_plus(nafSource)
    totalNAFrequest = httpNAFRequestString + nafSourceURLcoded
    # Generate request
    r = requests.get(totalNAFrequest)
    # Check if request is OK (200)
    if r.ok:
        print("200 OK")
    else:
        print("Request NOT OK!")
    # Startup browser and login
    startup_browser_and_login_to_unionVMS(self)

    # Select Positions tab
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"uvms-header-menu-item-movement"))).click()

    # Enter IRCS for newly created position
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"(//button[@type='button'])[2]"))).click()
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.LINK_TEXT,"Custom"))).click()
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"//input[@type='text']"))).clear()
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"//input[@type='text']"))).send_keys(ircsValue[0])

    # Click on search button
    WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"(//button[@type='submit'])[2]"))).click()
    time.sleep(browserWaitAfterClick)

    # Enter Vessel to verify position data
    self.assertEqual(countryValue, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"td[title=\"" + countryValue + "\"]"))).text)
    self.assertEqual(externalMarkingValue,
                     WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"td[title=\"" + externalMarkingValue + "\"]"))).text)
    self.assertEqual(ircsValue[0], WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"td[title=\"" + ircsValue[0] + "\"]"))).text)
    self.assertEqual(vesselName[0], WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.LINK_TEXT,vesselName[0]))).text)
    # Bug UVMS-3249 self.assertEqual(earlierPositionDateTimeValueString, WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"//*[@id='content']/div[1]/div[3]/div[2]/div/div[2]/div/div[4]/div/div/div/div/span/table/tbody/tr[1]/td[6]"))).text)
    self.assertEqual(lolaPositionValues[0][0][0], WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"td[title=\"" + lolaPositionValues[0][0][0] + "\"]"))).text)
    self.assertEqual(lolaPositionValues[0][0][1], WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"td[title=\"" + lolaPositionValues[0][0][1] + "\"]"))).text)
    self.assertEqual("%.2f" % speedValue + " kts", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"td[title=\"" + "%.2f" % speedValue + " kts" + "\"]"))).text)
    self.assertEqual(str(courseValue) + "°", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"td[title=\"" + str(courseValue) + "°" + "\"]"))).text)
    self.assertEqual(sourceValue[0], WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"td[title=\"" + sourceValue[0] + "\"]"))).text)

    return earlierPositionDateTimeValueString

def generate_NAF_string(self,countryValue,ircsValue,cfrValue,externalMarkingValue,latValue,longValue,speedValue,courseValue,dateValue,timeValue,vesselNameValue):
    # Generate NAF string to send
    nafSource = '//SR//FR/'
    nafSource = nafSource + countryValue
    nafSource = nafSource + "//AD/UVM//TM/POS//RC/"
    nafSource = nafSource + ircsValue
    nafSource = nafSource + "//IR/"
    nafSource = nafSource + cfrValue
    nafSource = nafSource + "//XR/"
    nafSource = nafSource + externalMarkingValue
    nafSource = nafSource + "//LT/"
    nafSource = nafSource + latValue
    nafSource = nafSource + "//LG/"
    nafSource = nafSource + longValue
    nafSource = nafSource + "//SP/"
    nafSource = nafSource + str(speedValue * 10)
    nafSource = nafSource + "//CO/"
    nafSource = nafSource + str(courseValue)
    nafSource = nafSource + "//DA/"
    nafSource = nafSource + dateValue
    nafSource = nafSource + "//TI/"
    nafSource = nafSource + timeValue
    nafSource = nafSource + "//NA/"
    nafSource = nafSource + vesselNameValue
    nafSource = nafSource + "//FS/"
    nafSource = nafSource + countryValue
    nafSource = nafSource + "//ER//"
    return nafSource




# -------------------------------------------------------------------------------------------------------------------
# -------------------------------------------------------------------------------------------------------------------
# -------------------------------------------------------------------------------------------------------------------
# -------------------------------------------------------------------------------------------------------------------
# -------------------------------------------------------------------------------------------------------------------
# -------------------------------------------------------------------------------------------------------------------
# T E S T    C A S E S
# -------------------------------------------------------------------------------------------------------------------
# -------------------------------------------------------------------------------------------------------------------
# -------------------------------------------------------------------------------------------------------------------
# -------------------------------------------------------------------------------------------------------------------
# -------------------------------------------------------------------------------------------------------------------
# -------------------------------------------------------------------------------------------------------------------

class UnionVMSTestCase(unittest.TestCase):

    def test_01_simple_login_union_vms(self):
        startup_browser_and_login_to_unionVMS(self,"vms_admin_com","password","AdminAll")
        shutdown_browser(self)

    def test_02_create_one_new_asset_and_validate(self):
        countryValue = 'SWE'
        homeportValue = "GOT"
        lengthValue = "14"
        grossTonnageValue = "3"
        powerValue = "1300"
        gearTypeValue = "Dermersal"
        mmsiValue = "" + str(random.randint(100000000, 999999999))
        imoValue = "0" + str(random.randint(100000,999999))
        commonRandomValue= str(random.randint(1000,9999))
        cfrValue = "SWE0000F" + commonRandomValue
        externalMarkingValue = "EXT3"
        vesselName = "Ship" + commonRandomValue
        ircsValue = "F" + commonRandomValue

        licenseTypeValue = "MOCK-license-DB"

        namePrefix = ["Glen", "Conny", "Sonny", "Kal", "Ada", "Osborn", "Rustan", "Reine", "Kent", "Frank"]

        producernameValue = random.choice(namePrefix)
        lastName = random.choice(namePrefix)
        producercodeValue = ""
        contactNameValue = producernameValue +" " + lastName + "son"
        contactEmailValue = producernameValue + "." +lastName + "son" + "@havochvatten.se"
        contactPhoneNumberValue = "+46720" + str(random.randint(100000,999999))

        startup_browser_and_login_to_unionVMS(self,"vms_admin_com","password","AdminAll")
        create_one_new_asset_from_gui(self, ircsValue,vesselName,externalMarkingValue,cfrValue,imoValue,homeportValue,mmsiValue,lengthValue,grossTonnageValue,powerValue,producernameValue,producercodeValue,contactNameValue,contactEmailValue,contactPhoneNumberValue)
        check_new_asset_exists(self,countryValue,gearTypeValue,licenseTypeValue, ircsValue,vesselName,externalMarkingValue,cfrValue,imoValue,homeportValue,mmsiValue,lengthValue,grossTonnageValue,powerValue,producernameValue,producercodeValue,contactNameValue,contactEmailValue,contactPhoneNumberValue)
        shutdown_browser(self)

    def test_03_create_one_new_mobile_terminal_and_validate(self):
        serialNoValue = "M" + str(random.randint(1000,9999))
        transceiverType = "Type A"
        softwareVersion = "A"
        antennaVersion = "A"
        dnidNumber = str(random.randint(1000,9999))
        satelliteNumber = "S" + dnidNumber
        memberIdnumber = "100"
        installedByName = "Mike Great"
        expectedFrequencyHours = "2"
        expectedFrequencyMinutes = "0"
        gracePeriodFrequencyHours = "15"
        gracePeriodFrequencyMinutes = "0"
        inPortFrequencyHours = "3"
        inPortFrequencyMinutes = "0"

        populateIridiumImarsatCData()

        startup_browser_and_login_to_unionVMS(self,"vms_admin_com","password","AdminAll")
        create_one_new_mobile_terminal_from_gui(self, serialNoValue, transceiverType, softwareVersion, antennaVersion, satelliteNumber, dnidNumber, memberIdnumber, installedByName, expectedFrequencyHours, gracePeriodFrequencyHours, inPortFrequencyHours)
        check_new_mobile_terminal_exists(self, serialNoValue, memberIdnumber, dnidNumber, transceiverType, softwareVersion, satelliteNumber, antennaVersion, installedByName)
        shutdown_browser(self)


    def test_04_link_asset_and_mobile_terminal(self):
        #Asset
        countryValue = 'SWE'
        homeportValue = "GOT"
        lengthValue = "14"
        grossTonnageValue = "3"
        powerValue = "1300"
        gearTypeValue = "Dermersal"
        mmsiValue = "" + str(random.randint(100000000, 999999999))
        imoValue = "0" + str(random.randint(100000,999999))
        commonRandomValue= str(random.randint(1000,9999))
        cfrValue = "SWE0000F" + commonRandomValue
        externalMarkingValue = "EXT3"
        vesselName = "Ship" + commonRandomValue
        ircsValue = "F" + commonRandomValue

        licenseTypeValue = "MOCK-license-DB"

        namePrefix = ["Glen", "Conny", "Sonny", "Kal", "Ada", "Osborn", "Rustan", "Reine", "Kent", "Frank"]

        producernameValue = random.choice(namePrefix)
        lastName = random.choice(namePrefix)
        producercodeValue = ""
        contactNameValue = producernameValue +" " + lastName + "son"
        contactEmailValue = producernameValue + "." +lastName + "son" + "@havochvatten.se"
        contactPhoneNumberValue = "+46720" + str(random.randint(100000,999999))

        #MobileTerminal
        serialNoValue = "M" + str(random.randint(1000,9999))
        transceiverType = "Type A"
        softwareVersion = "A"
        antennaVersion = "A"
        dnidNumber = str(random.randint(1000,9999))
        satelliteNumber = "S" + dnidNumber
        memberIdnumber = "100"
        installedByName = "Mike Great"
        expectedFrequencyHours = "2"
        expectedFrequencyMinutes = "0"
        gracePeriodFrequencyHours = "15"
        gracePeriodFrequencyMinutes = "0"
        inPortFrequencyHours = "3"
        inPortFrequencyMinutes = "0"



        startup_browser_and_login_to_unionVMS(self,"vms_admin_com","password","AdminAll")
        create_one_new_asset_from_gui(self, ircsValue,vesselName,externalMarkingValue,cfrValue,imoValue,homeportValue,mmsiValue,lengthValue,grossTonnageValue,powerValue,producernameValue,producercodeValue,contactNameValue,contactEmailValue,contactPhoneNumberValue)
        check_new_asset_exists(self,countryValue,gearTypeValue,licenseTypeValue, ircsValue,vesselName,externalMarkingValue,cfrValue,imoValue,homeportValue,mmsiValue,lengthValue,grossTonnageValue,powerValue,producernameValue,producercodeValue,contactNameValue,contactEmailValue,contactPhoneNumberValue)

        populateIridiumImarsatCData()

        create_one_new_mobile_terminal_from_gui(self, serialNoValue, transceiverType, softwareVersion, antennaVersion, satelliteNumber, dnidNumber, memberIdnumber, installedByName, expectedFrequencyHours, gracePeriodFrequencyHours, inPortFrequencyHours)
        check_new_mobile_terminal_exists(self, serialNoValue, memberIdnumber, dnidNumber, transceiverType, softwareVersion, satelliteNumber, antennaVersion, installedByName)

        populateIridiumImarsatCData()

        link_asset_and_mobile_terminal(self,serialNoValue, ircsValue)
        shutdown_browser(self)


    def test_05_link_asset_and_mobile_terminal_and_unlink(self):
            #Asset
        countryValue = 'SWE'
        homeportValue = "GOT"
        lengthValue = "14"
        grossTonnageValue = "3"
        powerValue = "1300"
        gearTypeValue = "Dermersal"
        mmsiValue = "" + str(random.randint(100000000, 999999999))
        imoValue = "0" + str(random.randint(100000,999999))
        commonRandomValue= str(random.randint(1000,9999))
        cfrValue = "SWE0000F" + commonRandomValue
        externalMarkingValue = "EXT3"
        vesselName = "Ship" + commonRandomValue
        ircsValue = "F" + commonRandomValue

        licenseTypeValue = "MOCK-license-DB"

        namePrefix = ["Glen", "Conny", "Sonny", "Kal", "Ada", "Osborn", "Rustan", "Reine", "Kent", "Frank"]

        producernameValue = random.choice(namePrefix)
        lastName = random.choice(namePrefix)
        producercodeValue = ""
        contactNameValue = producernameValue +" " + lastName + "son"
        contactEmailValue = producernameValue + "." +lastName + "son" + "@havochvatten.se"
        contactPhoneNumberValue = "+46720" + str(random.randint(100000,999999))

        #MobileTerminal
        serialNoValue = "M" + str(random.randint(1000,9999))
        transceiverType = "Type A"
        softwareVersion = "A"
        antennaVersion = "A"
        dnidNumber = str(random.randint(1000,9999))
        satelliteNumber = "S" + dnidNumber
        memberIdnumber = "100"
        installedByName = "Mike Great"
        expectedFrequencyHours = "2"
        expectedFrequencyMinutes = "0"
        gracePeriodFrequencyHours = "15"
        gracePeriodFrequencyMinutes = "0"
        inPortFrequencyHours = "3"
        inPortFrequencyMinutes = "0"

        startup_browser_and_login_to_unionVMS(self,"vms_admin_com","password","AdminAll")
        create_one_new_asset_from_gui(self, ircsValue,vesselName,externalMarkingValue,cfrValue,imoValue,homeportValue,mmsiValue,lengthValue,grossTonnageValue,powerValue,producernameValue,producercodeValue,contactNameValue,contactEmailValue,contactPhoneNumberValue)
        check_new_asset_exists(self,countryValue,gearTypeValue,licenseTypeValue, ircsValue,vesselName,externalMarkingValue,cfrValue,imoValue,homeportValue,mmsiValue,lengthValue,grossTonnageValue,powerValue,producernameValue,producercodeValue,contactNameValue,contactEmailValue,contactPhoneNumberValue)

        populateIridiumImarsatCData()

        create_one_new_mobile_terminal_from_gui(self, serialNoValue, transceiverType, softwareVersion, antennaVersion, satelliteNumber, dnidNumber, memberIdnumber, installedByName, expectedFrequencyHours, gracePeriodFrequencyHours, inPortFrequencyHours)
        check_new_mobile_terminal_exists(self, serialNoValue, memberIdnumber, dnidNumber, transceiverType, softwareVersion, satelliteNumber, antennaVersion, installedByName)

        populateIridiumImarsatCData()

        link_asset_and_mobile_terminal(self,serialNoValue, ircsValue)

        # Select Mobile Terminal tab
        WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"uvms-header-menu-item-communication"))).click()

        # Enter Serial Number in field
        WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-input-search-serialNumber"))).clear()
        WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-input-search-serialNumber"))).send_keys(serialNoValue)
        # Click in search button
        WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-btn-advanced-search"))).click()
        time.sleep(browserWaitAfterClick)
        # Click on details button
        WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"mt-toggle-form"))).click()

        # Click on unlinking button
        WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"menu-bar-unlink"))).click()

        # Enter comment and click on unlinking button
        WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.NAME,"comment"))).send_keys("Unlink Asset and MT.")
        WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"div.modal-footer > div.row > div.col-md-12 > button.btn.btn-primary"))).click()

        # Shutdown browser
        shutdown_browser(self)


    def test_06_generate_and_verify_manual_position(self):
        countryValue = 'SWE'
        homeportValue = "GOT"
        lengthValue = "14"
        grossTonnageValue = "3"
        powerValue = "1300"
        gearTypeValue = "Dermersal"
        mmsiValue = "" + str(random.randint(100000000, 999999999))
        imoValue = "0" + str(random.randint(100000,999999))
        commonRandomValue= str(random.randint(1000,9999))
        cfrValue = "SWE0000F" + commonRandomValue
        externalMarkingValue = "EXT3"
        vesselName = "Ship" + commonRandomValue
        ircsValue = "F" + commonRandomValue

        licenseTypeValue = "MOCK-license-DB"

        namePrefix = ["Glen", "Conny", "Sonny", "Kal", "Ada", "Osborn", "Rustan", "Reine", "Kent", "Frank"]

        producernameValue = random.choice(namePrefix)
        lastName = random.choice(namePrefix)
        producercodeValue = ""
        contactNameValue = producernameValue +" " + lastName + "son"
        contactEmailValue = producernameValue + "." +lastName + "son" + "@havochvatten.se"
        contactPhoneNumberValue = "+46720" + str(random.randint(100000,999999))

        startup_browser_and_login_to_unionVMS(self,"vms_admin_com","password","AdminAll")
        create_one_new_asset_from_gui(self, ircsValue,vesselName,externalMarkingValue,cfrValue,imoValue,homeportValue,mmsiValue,lengthValue,grossTonnageValue,powerValue,producernameValue,producercodeValue,contactNameValue,contactEmailValue,contactPhoneNumberValue)
        check_new_asset_exists(self,countryValue,gearTypeValue,licenseTypeValue, ircsValue,vesselName,externalMarkingValue,cfrValue,imoValue,homeportValue,mmsiValue,lengthValue,grossTonnageValue,powerValue,producernameValue,producercodeValue,contactNameValue,contactEmailValue,contactPhoneNumberValue)

        reportedSpeedValue = 5
        reportedCourseValue = 180
        deltaTimeValue=2
        # lolaPositionValues [Asset number x, lola position route y, lat=0/lon=1 z]
        lolaPositionValues = [[["56° 30,661′", "11° 30,820′"], ["56° 42,270′", "11° 20,273′"]],
                              [["57.934", "11.592"], ["57.935", "11.593"]],
                              [["56.647", "12.840"], ["56.646", "12.834"]],
                              [["56.659", "16.378"], ["56.659", "16.381"]],
                              [["57.266", "16.480"], ["57.267", "16.487"]],
                              [["58.662", "17.129"], ["58.661", "17.137"]],
                              [["58.662", "17.129"], ["58.661", "17.137"]],
                              [["57.554", "11.893"], ["57.554", "11.893"]],
                              [["63.703", "20.621"], ["63.703", "20.621"]],
                              [["63.659", "20.608"], ["63.659", "20.608"]],
                              [["63.624", "20.598"], ["63.624", "20.598"]],
                              [["63.597", "20.602"], ["63.597", "20.602"]],
                              [["63.577", "20.603"], ["63.577", "20.603"]],
                              [["63.553", "20.599"], ["63.553", "20.599"]],
                              [["63.519", "20.553"], ["63.519", "20.553"]],
                              [["63.500", "20.547"], ["63.500", "20.547"]],
                              [["63.449", "20.510"], ["63.449", "20.510"]],
                              [["63.403", "20.457"], ["63.403", "20.457"]]]
        sourceValue = "MANUAL"

        generate_and_verify_manual_position(self, reportedSpeedValue, reportedCourseValue,ircsValue,cfrValue,lolaPositionValues,countryValue,externalMarkingValue,vesselName,sourceValue,deltaTimeValue);
        # Shutdown browser
        shutdown_browser(self)


    def test_27_view_audit_log(self):
        # Startup browser and login
        startup_browser_and_login_to_unionVMS(self,"vms_admin_com","password","AdminAll")
        # Select Audit Log tab
        time.sleep(browserWaitAfterClick)
                
        WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"uvms-header-menu-item-audit-log")))
                
        action=ActionChains(self.driver)
        action.move_to_element(self.driver.find_element_by_id("uvms-header-menu-item-audit-log")).perform() 
        
        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"uvms-header-menu-item-audit-log"))).click()
        time.sleep(browserWaitAfterClick)

        # Check sub tab names
        self.assertEqual("ALL", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"#ALL > span"))).text)
        self.assertEqual("EXCHANGE", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"#EXCHANGE > span"))).text)
        self.assertEqual("POSITION REPORTS", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"#POSITION_REPORTS > span"))).text)
        self.assertEqual("ASSETS AND TERMINALS", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"#ASSETS_AND_TERMINALS > span"))).text)
        self.assertEqual("GIS", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"#GIS > span"))).text)
        self.assertEqual("ALERTS", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"#ALARMS > span"))).text)
        self.assertEqual("ACCESS CONTROL", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"#ACCESS_CONTROL > span"))).text)

        # Click on all sub tabs under Audit Log Tab
        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR ,"#EXCHANGE > span"))).click()

        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR ,"#POSITION_REPORTS > span"))).click()

        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR ,"#ASSETS_AND_TERMINALS > span"))).click()

        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR ,"#GIS > span"))).click()

        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR ,"#ALARMS > span"))).click()

        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR ,"#ACCESS_CONTROL > span"))).click()

        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR ,"#ALL > span"))).click()

        # Shutdown browser
        shutdown_browser(self)




    def test_29_view_configuration_pages(self):
        # Startup browser and login
        startup_browser_and_login_to_unionVMS(self,"vms_admin_com","password","AdminAll")
        # Select Admin tab
        time.sleep(browserWaitAfterClick)
        
        WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.ID,"uvms-header-menu-item-audit-log")))
        
        action=ActionChains(self.driver)
        action.move_to_element(self.driver.find_element_by_id("uvms-header-menu-item-audit-log")).perform() 

        
        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"uvms-header-menu-item-audit-log"))).click()
        time.sleep(browserWaitAfterClick)

        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.LINK_TEXT,"CONFIGURATION"))).click()

        # Check sub tab names
        self.assertEqual("SYSTEM MONITOR", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"#systemMonitor > span"))).text)
        self.assertEqual("GLOBAL SETTINGS", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"#globalSettings > span"))).text)
        self.assertEqual("REPORTING", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"#reporting > span"))).text)
        self.assertEqual("ASSETS", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"#asset > span"))).text)
        self.assertEqual("MOBILE TERMINALS", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"#mobileTerminal > span"))).text)
        self.assertEqual("EXCHANGE", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.CSS_SELECTOR ,"#exchange > span"))).text)

        # Click on all sub tabs under Configuration Tab
        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR ,"#systemMonitor > span"))).click()

        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR ,"#globalSettings > span"))).click()

        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR ,"#reporting > span"))).click()

        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR ,"#asset > span"))).click()

        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR ,"#mobileTerminal > span"))).click()

        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CSS_SELECTOR ,"#exchange > span"))).click()

        # Shutdown browser
        shutdown_browser(self)


    def test_32_check_view_help_text(self):
        # Startup browser and login
        startup_browser_and_login_to_unionVMS(self,"vms_admin_com","password","AdminAll")
        # Click on User Guide icon (Question mark icon)
        # Note: User Guide page is opened in a new tab
        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.CLASS_NAME,"help"))).click()
        time.sleep(10)
        # Switch tab focus for Selenium to the new tab
        self.driver.switch_to.window(self.driver.window_handles[-1])

        # Check User guide page
        self.assertEqual("Union VMS - User Manual", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"//*[@id='title-text']/a"))).text)

        self.assertEqual("Welcome to Union VMS!", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"//*[@id='main-content']/div[3]/ul/li[1]/span/a"))).text)
        # Shutdown browser
        shutdown_browser(self)

    def test_33_check_alerts_view(self):
        # Startup browser and login
        startup_browser_and_login_to_unionVMS(self,"vms_admin_com","password","AdminAll")
        # Select Alerts tab (Holding Table)
        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.ID,"uvms-header-menu-item-holding-table"))).click()
        time.sleep(browserWaitAfterClick)


        # Check List Headlines for Holding Table
        self.assertEqual("Date triggered (UTC)", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH ,"//*[@id='content']/div[1]/div[3]/div[2]/div/div[2]/div/div[3]/div/div/div/div/span/table/thead/tr/th[2]/a/span/span"))).text)
        self.assertEqual("Object affected", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"//div[@id='content']/div/div[3]/div[2]/div/div[2]/div/div[3]/div/div/div/div/span/table/thead/tr/th[3]/a/span/span"))).text)
        self.assertEqual("Rule", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"//div[@id='content']/div/div[3]/div[2]/div/div[2]/div/div[3]/div/div/div/div/span/table/thead/tr/th[4]"))).text)
        # Select Alerts tab (Notifications)
        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.XPATH,"//*[@id='content']/div[1]/div[3]/div[2]/div/div[1]/div/div/ul/li[2]/a"))).click()
        time.sleep(browserWaitAfterClick)

        # Check List Headlines for Notifications
        self.assertEqual("Date triggered (UTC)", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH ,"//*[@id='content']/div[1]/div[3]/div[2]/div/div[2]/div/div[3]/div/div/div/div/span/table/thead/tr/th[2]/a/span/span"))).text)
        self.assertEqual("Object affected", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"//div[@id='content']/div/div[3]/div[2]/div/div[2]/div/div[3]/div/div/div/div/span/table/thead/tr/th[3]/a/span/span"))).text)
        self.assertEqual("Rule", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"//div[@id='content']/div/div[3]/div[2]/div/div[2]/div/div[3]/div/div/div/div/span/table/thead/tr/th[4]"))).text)
        # Select Alerts tab (Rules)
        WebDriverWait(self.driver, browserTimeout).until(EC.element_to_be_clickable((By.XPATH,"//*[@id='content']/div[1]/div[3]/div[2]/div/div[1]/div/div/ul/li[3]/a"))).click()
        time.sleep(browserWaitAfterClick)

        # Check List Headlines for Rules List
        self.assertEqual("Rule name", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH ,"//*[@id='content']/div[1]/div[3]/div[2]/div/div[2]/div/div[3]/div/div/div/div/span/table/thead/tr/th[2]/a/span/span"))).text)
        self.assertEqual("Last triggered", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"//div[@id='content']/div/div[3]/div[2]/div/div[2]/div/div[3]/div/div/div/div/span/table/thead/tr/th[3]/a/span/span"))).text)
        self.assertEqual("Date updated", WebDriverWait(self.driver, browserTimeout).until(EC.presence_of_element_located((By.XPATH,"//div[@id='content']/div/div[3]/div[2]/div/div[2]/div/div[3]/div/div/div/div/span/table/thead/tr/th[4]/a/span/span"))).text)
        # Shutdown browser
        shutdown_browser(self)

#if __name__ == '__main__':
#    unittest.main()

if __name__ == '__main__':
    unittest.main(
        testRunner=xmlrunner.XMLTestRunner(output='target/failsafe-reports'),
        # these make sure that some options that are not applicable
        # remain hidden from the help menu.
        failfast=False, buffer=False, catchbreak=False)

#unittest.main()
