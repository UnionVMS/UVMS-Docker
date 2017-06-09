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
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
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
    # Start Chrome browser
    cls.driver = webdriver.Chrome()
    #cls.driver = webdriver.Firefox()
    # Maximize browser window
    cls.driver.maximize_window()
    # Login to test user admin
    #cls.driver.get("https://unionvmstest.havochvatten.se/unionvms/")
    cls.driver.get(httpUnionVMSurlString)
    time.sleep(2)

    # if Hav och vatten proxy page is presented, then autologin
    try:
        if cls.driver.find_element_by_xpath("/html/head/title"):
            cls.driver.switch_to.frame("content")
            cls.driver.find_element_by_css_selector("img[alt=\"Automatisk inloggning\"]").click()
            time.sleep(2)
    except:
        pass


    # if Pop-up windows exists then click cancel
    try:
        if cls.driver.find_element_by_xpath("/html/body/div[5]/div/div/div/form"):
            cls.driver.find_element_by_xpath("/html/body/div[5]/div/div/div/form/div[3]/button[2]").click()
            time.sleep(2)
    except:
        pass

    cls.driver.find_element_by_id("userId").send_keys("vms_admin_com")
    cls.driver.find_element_by_id("password").send_keys("password")
    time.sleep(2)
    cls.driver.find_element_by_xpath(
        "//*[@id='content']/div[1]/div[3]/div/div[2]/div[3]/div[2]/form/div[3]/div/button"). \
        click()
    time.sleep(2)
    cls.driver.find_element_by_partial_link_text("AdminAll").click()
    time.sleep(2)


def shutdown_browser(cls):
    cls.driver.quit()


def create_one_new_asset_from_gui(self,ircsValue,vesselName,externalMarkingValue,cfrValue,imoValue,homeportValue,mmsiValue,lengthValue,grossTonnageValue,powerValue,producernameValue,producercodeValue,contactNameValue,contactEmailValue,contactPhoneNumberValue):
    # Startup browser and login
    self.driver.implicitly_wait(10)
    time.sleep(5)
    # Click on asset tab
    self.driver.find_element_by_id("uvms-header-menu-item-assets").click()
    time.sleep(1)
    # Click on new Asset button
    self.driver.find_element_by_id("asset-btn-create").click()
    time.sleep(2)
    # Select F.S value
    self.driver.find_element_by_id("asset-input-countryCode").click()
    self.driver.find_element_by_id("asset-input-countryCode-item-2").click()
    # Enter IRCS value
    self.driver.find_element_by_id("asset-input-ircs").send_keys(ircsValue)
    # Enter Name value
    self.driver.find_element_by_id("asset-input-name").send_keys(vesselName)
    # Enter External Marking Value
    self.driver.find_element_by_id("asset-input-externalMarking").send_keys(externalMarkingValue)
    # Enter CFR Value
    self.driver.find_element_by_id("asset-input-cfr").send_keys(cfrValue)
    # Enter IMO Value
    self.driver.find_element_by_id("asset-input-imo").send_keys(imoValue)
    # Enter HomePort Value
    self.driver.find_element_by_id("asset-input-homeport").send_keys(homeportValue)
    # Select Gear Type value
    self.driver.find_element_by_id("asset-input-gearType").click()
    self.driver.find_element_by_id("asset-input-gearType-item-0").click()
    # Enter MMSI Value
    self.driver.find_element_by_id("asset-input-mmsi").send_keys(mmsiValue)
    # Select License Type value
    self.driver.find_element_by_id("asset-input-licenseType").click()
    time.sleep(1)
    self.driver.find_element_by_id("asset-input-licenseType-item-0").click()
    # Length Value
    self.driver.find_element_by_id("asset-input-lengthValue").send_keys(lengthValue)
    # Gross Tonnage Value
    self.driver.find_element_by_id("asset-input-grossTonnage").send_keys(grossTonnageValue)
    # Main Power Value
    self.driver.find_element_by_id("asset-input-power").send_keys(powerValue)
    # Main Producer Name Value
    self.driver.find_element_by_id("asset-input-producername").send_keys(producernameValue)
    # Main Producer Code Value
    self.driver.find_element_by_id("asset-input-producercode").send_keys(producercodeValue)
    # Click on the Contacts tab
    self.driver.find_element_by_xpath("//*[@id='CONTACTS']/span").click()
    time.sleep(1)
    # Main Contact Name Value
    self.driver.find_element_by_id("asset-input-contact-name-0").send_keys(contactNameValue)
    # Main E-mail Value
    self.driver.find_element_by_id("asset-input-contact-email-0").send_keys(contactEmailValue)
    # Main Contact Number Value
    self.driver.find_element_by_id("asset-input-contact-number-0").send_keys(contactPhoneNumberValue)
    # Click on Save Asset button
    self.driver.find_element_by_id("menu-bar-save").click()
    time.sleep(5)
    # Leave new asset view
    self.driver.find_element_by_id("menu-bar-cancel").click()
    time.sleep(3)


def create_one_new_mobile_terminal_from_gui(self, serialNoValue, transceiverType, softwareVersion, antennaVersion, satelliteNumber, dnidNumber, memberIdnumber, installedByName, expectedFrequencyHours, gracePeriodFrequencyHours, inPortFrequencyHours):
    # Startup browser and login
    self.driver.find_element_by_id("uvms-header-menu-item-communication").click()
    time.sleep(2)
    # Click on new terminal button
    self.driver.find_element_by_id("mt-btn-create").click()
    time.sleep(3)
    # Select Transponder system
    self.driver.find_element_by_id("mt-0-typeAndPlugin").click()
    time.sleep(1)
    self.driver.find_element_by_link_text("Inmarsat-C : twostage").click()
    time.sleep(1)
    # Enter serial number
    self.driver.find_element_by_id("mt-0-serialNumber").send_keys(serialNoValue)
    # Enter Transceiver type
    self.driver.find_element_by_id("mt-0-tranciverType").send_keys(transceiverType)
    # Enter Software Version
    self.driver.find_element_by_id("mt-0-softwareVersion").send_keys(softwareVersion)
    # Enter Antenna
    self.driver.find_element_by_id("mt-0-antenna").send_keys(antennaVersion)
    # Enter Satellite Number
    self.driver.find_element_by_id("mt-0-satelliteNumber").send_keys(satelliteNumber)
    # Enter DNID Number
    self.driver.find_element_by_name("dnid").send_keys(dnidNumber)
    # Enter Member Number
    self.driver.find_element_by_name("memberId").send_keys(memberIdnumber)
    # Enter Installed by
    self.driver.find_element_by_id("mt-0-channel-0-installedBy").send_keys(installedByName)
    # Expected frequency
    self.driver.find_element_by_id("mt-0-channel-0-frequencyExpected").send_keys(expectedFrequencyHours)
    # Grace period
    self.driver.find_element_by_id("mt-0-channel-0-frequencyGrace").send_keys(gracePeriodFrequencyHours)
    # In port
    self.driver.find_element_by_id("mt-0-channel-0-frequencyPort").send_keys(inPortFrequencyHours)
    time.sleep(2)
    # Activate Mobile Terminal button
    self.driver.find_element_by_id("mt-0-activation").click()
    time.sleep(5)
    # Click on save button
    self.driver.find_element_by_id("menu-bar-save").click()
    time.sleep(5)
    # Leave new asset view
    self.driver.find_element_by_id("menu-bar-cancel").click()
    time.sleep(2)
    

def create_one_new_mobile_terminal_via_asset_tab(self, mobileTerminalNumber, vesselNumber):
    # Startup browser and login
    startup_browser_and_login_to_unionVMS(self)
    time.sleep(5)
    # Click on asset tab
    self.driver.find_element_by_id("uvms-header-menu-item-assets").click()
    time.sleep(1)
    # Search for created asset
    self.driver.find_element_by_id("asset-input-simple-search").clear()
    self.driver.find_element_by_id("asset-input-simple-search").send_keys(vesselName[vesselNumber])
    time.sleep(1)
    self.driver.find_element_by_id("asset-btn-simple-search").click()
    time.sleep(3)
    # Click on details button
    self.driver.find_element_by_id("asset-toggle-form").click()
    time.sleep(1)
    # Click on add new terminal button
    self.driver.find_element_by_id("menu-bar-vessel-add-terminal").click()
    time.sleep(1)
    # Select Transponder system
    self.driver.find_element_by_id("mt-0-typeAndPlugin").click()
    time.sleep(1)
    self.driver.find_element_by_link_text("Inmarsat-C : twostage").click()
    time.sleep(1)
    # Enter serial number
    self.driver.find_element_by_id("mt-0-serialNumber").send_keys(serialNoValue[mobileTerminalNumber])
    # Enter Transceiver type
    self.driver.find_element_by_id("mt-0-tranciverType").send_keys(transceiverType[mobileTerminalNumber])
    # Enter Software Version
    self.driver.find_element_by_id("mt-0-softwareVersion").send_keys(softwareVersion)
    # Enter Antenna
    self.driver.find_element_by_id("mt-0-antenna").send_keys(antennaVersion)
    # Enter Satellite Number
    self.driver.find_element_by_id("mt-0-satelliteNumber").send_keys(satelliteNumber[mobileTerminalNumber])
    # Enter DNID Number
    self.driver.find_element_by_name("dnid").send_keys(dnidNumber[mobileTerminalNumber])
    # Enter Member Number
    self.driver.find_element_by_name("memberId").send_keys(memberIdnumber[mobileTerminalNumber])
    # Enter Installed by
    self.driver.find_element_by_id("mt-0-channel-0-installedBy").send_keys(installedByName)
    # Expected frequency
    self.driver.find_element_by_id("mt-0-channel-0-frequencyExpected").send_keys(expectedFrequencyHours)
    # Grace period
    self.driver.find_element_by_id("mt-0-channel-0-frequencyGrace").send_keys(gracePeriodFrequencyHours)
    # In port
    self.driver.find_element_by_id("mt-0-channel-0-frequencyPort").send_keys(inPortFrequencyHours)
    # Activate Mobile Terminal button
    self.driver.find_element_by_id("mt-0-activation").click()
    time.sleep(3)
    # Click on save button
    self.driver.find_element_by_xpath("//*[@id='menu-bar-update']").click()
    time.sleep(5)
    # Leave new asset view
    self.driver.find_element_by_id("menu-bar-cancel").click()
    time.sleep(2)
    # Shutdown browser
    shutdown_browser(self)


def check_new_asset_exists(self,countryValue,gearTypeValue,licenseTypeValue, ircsValue,vesselName,externalMarkingValue,cfrValue,imoValue,homeportValue,mmsiValue,lengthValue,grossTonnageValue,powerValue,producernameValue,producercodeValue,contactNameValue,contactEmailValue,contactPhoneNumberValue):
    # Startup browser and login
    self.driver.find_element_by_id("uvms-header-menu-item-assets").click()
    time.sleep(5)
    # Search for the new created asset in the asset list
    self.driver.find_element_by_id("asset-input-simple-search").send_keys(vesselName)
    self.driver.find_element_by_id("asset-btn-simple-search").click()
    time.sleep(5)
    # Check that the new asset exists in the list.
    self.assertEqual(vesselName, self.driver.find_element_by_css_selector("td[title=\"" + vesselName + "\"]").text)
    time.sleep(1)
    # Click on details button for new asset
    self.driver.find_element_by_id("asset-toggle-form").click()
    time.sleep(5)
    # Check that the F.S value is correct.
    self.assertEqual(countryValue, self.driver.find_element_by_id("asset-input-countryCode").text)
    # Check that the IRCS value is correct
    self.assertEqual(ircsValue, self.driver.find_element_by_id("asset-input-ircs").get_attribute("value"))
    # Check that the Name value is correct
    self.assertEqual(vesselName, self.driver.find_element_by_id("asset-input-name").get_attribute("value"))
    # Check that External Marking Value is correct
    self.assertEqual(externalMarkingValue, self.driver.find_element_by_id("asset-input-externalMarking").get_attribute("value"))
    # Check that the CFR value is correct
    self.assertEqual(cfrValue, self.driver.find_element_by_id("asset-input-cfr").get_attribute("value"))
    # Check that the IMO value is correct
    self.assertEqual(imoValue, self.driver.find_element_by_id("asset-input-imo").get_attribute("value"))
    # Check that the HomePort value is correct
    self.assertEqual(homeportValue, self.driver.find_element_by_id("asset-input-homeport").get_attribute("value"))
    # Check that the Gear Type value is correct.
    self.assertEqual(gearTypeValue, self.driver.find_element_by_id("asset-input-gearType").text)
    # Check that the MMSI value is correct
    self.assertEqual(mmsiValue, self.driver.find_element_by_id("asset-input-mmsi").get_attribute("value"))
    # Check that the License Type value is correct.
    self.assertEqual(licenseTypeValue, self.driver.find_element_by_id("asset-input-licenseType").text)
    # Check that the Length Type value is correct.
    self.assertEqual(lengthValue, self.driver.find_element_by_id("asset-input-lengthValue").get_attribute("value"))
    # Check that the Gross Tonnage value is correct.
    self.assertEqual(grossTonnageValue, self.driver.find_element_by_id("asset-input-grossTonnage").get_attribute("value"))
    # Check that the Power value is correct.
    self.assertEqual(powerValue, self.driver.find_element_by_id("asset-input-power").get_attribute("value"))
    # Check that the Producer Name value is correct.
    #
    # Needs to be updated according to asset database
    #
    #
    # self.assertEqual("Mikael", self.driver.find_element_by_id("asset-input-producername").get_attribute("value"))
    # Check that the Producer Code value is correct.
    self.assertEqual(producercodeValue, self.driver.find_element_by_id("asset-input-producercode").get_attribute("value"))
    # Click on the Contacts tab
    self.driver.find_element_by_xpath("//*[@id='CONTACTS']/span").click()
    time.sleep(1)
    # Check that the Contact Name value is correct.
    self.assertEqual(contactNameValue, self.driver.find_element_by_id("asset-input-contact-name-0").get_attribute("value"))
    # Check that the E-mail value is correct.
    self.assertEqual(contactEmailValue, self.driver.find_element_by_id("asset-input-contact-email-0").get_attribute("value"))
    # Check that the E-mail value is correct.
    self.assertEqual(contactPhoneNumberValue, self.driver.find_element_by_id("asset-input-contact-number-0").get_attribute("value"))
    time.sleep(5)
    # Shutdown browser

def check_new_mobile_terminal_exists(self, serialNoValue, memberIdnumber, dnidNumber, transceiverType, softwareVersion, satelliteNumber, antennaVersion, installedByName):
    # Select Mobile Terminal tab
    self.driver.find_element_by_id("uvms-header-menu-item-communication").click()
    time.sleep(2)
    # Enter Serial Number in
    self.driver.find_element_by_xpath("(//input[@type='text'])[7]").send_keys(serialNoValue)
    # Click in search button
    self.driver.find_element_by_xpath("//button[@type='submit']").click()
    time.sleep(5)
    # Check Serial Number in the list
    self.assertEqual(serialNoValue, self.driver.find_element_by_xpath("//div[@id='content']/div/div[3]/div[2]/div/div/div/div/div[3]/div/div/div/div/span/table/tbody/tr/td[3]").text)
    # Check Member Number in the list
    self.assertEqual(memberIdnumber, self.driver.find_element_by_xpath("//div[@id='content']/div/div[3]/div[2]/div/div/div/div/div[3]/div/div/div/div/span/table/tbody/tr/td[4]").text)
    # Check DNID Number in the list
    self.assertEqual(dnidNumber, self.driver.find_element_by_xpath("//div[@id='content']/div/div[3]/div[2]/div/div/div/div/div[3]/div/div/div/div/span/table/tbody/tr/td[5]").text)
    # Click on details button
    self.driver.find_element_by_xpath("//div[@id='content']/div/div[3]/div[2]/div/div/div/div/div[3]/div/div/div/div/span/table/tbody/tr/td[10]/button").click()
    time.sleep(2)
    # Check Serial Number
    self.assertEqual(serialNoValue, self.driver.find_element_by_id("mt-0-serialNumber").get_attribute("value"))
    # Check Transceiver Type
    self.assertEqual(transceiverType, self.driver.find_element_by_id("mt-0-tranciverType").get_attribute("value"))
    # Check Software Version
    self.assertEqual(softwareVersion, self.driver.find_element_by_id("mt-0-softwareVersion").get_attribute("value"))
    # Check Satellite Number
    self.assertEqual(satelliteNumber, self.driver.find_element_by_id("mt-0-satelliteNumber").get_attribute("value"))
    # Check Antenna Version
    self.assertEqual(antennaVersion, self.driver.find_element_by_id("mt-0-antenna").get_attribute("value"))
    # Check DNID Number
    self.assertEqual(dnidNumber, self.driver.find_element_by_name("dnid").get_attribute("value"))
    # Check Member Number
    self.assertEqual(memberIdnumber, self.driver.find_element_by_name("memberId").get_attribute("value"))
    # Check Installed by Name
    self.assertEqual(installedByName, self.driver.find_element_by_id("mt-0-channel-0-installedBy").get_attribute("value"))
    # Leave new asset view
    self.driver.find_element_by_id("menu-bar-cancel").click()
    time.sleep(2)
    

def link_asset_and_mobile_terminal(self, serialNoValue, ircsValue):
    # Select Mobile Terminal tab
    self.driver.find_element_by_id("uvms-header-menu-item-communication").click()
    time.sleep(2)
    # Enter Serial Number in field
    self.driver.find_element_by_id("mt-input-search-serialNumber").clear()

    self.driver.find_element_by_id("mt-input-search-serialNumber").send_keys(serialNoValue)
    # Click in search button
    self.driver.find_element_by_id("mt-btn-advanced-search").click()
    time.sleep(5)
    # Click on details button
    self.driver.find_element_by_id("mt-toggle-form").click()
    time.sleep(3)
    # Click on Link Asset
    self.driver.find_element_by_id("mt-btn-assign-asset").click()
    time.sleep(2)
    # Enter Asset Name and clicks on the search button
    self.driver.find_element_by_xpath("(//input[@type='text'])[23]").send_keys(ircsValue)
    self.driver.find_element_by_xpath("//button[@type='submit']").click()
    time.sleep(2)
    # Click on connect button
    self.driver.find_element_by_css_selector("td.textAlignRight > button.btn.btn-primary").click()
    # Click on Link button
    time.sleep(2)
    self.driver.find_element_by_css_selector("div.col-md-6.textAlignRight > button.btn.btn-primary").click()
    # Enter Reason comment
    self.driver.find_element_by_name("comment").send_keys("Need to connect this mobile terminal with this asset.")
    time.sleep(2)
    # Click on Link button 2
    self.driver.find_element_by_css_selector("div.modal-footer > div.row > div.col-md-12 > button.btn.btn-primary").click()
    time.sleep(2)
    # Close page
    self.driver.find_element_by_id("menu-bar-cancel").click()
    time.sleep(2)


def change_and_check_speed_format(self,unitNumber):
    # Startup browser and login
    startup_browser_and_login_to_unionVMS(self)
    time.sleep(5)
    # Select Admin tab
    self.driver.find_element_by_id("uvms-header-menu-item-audit-log").click()
    time.sleep(5)
    self.driver.find_element_by_link_text("CONFIGURATION").click()
    time.sleep(3)
    # Click on Global setting subtab under Configuration Tab
    self.driver.find_element_by_css_selector("#globalSettings > span").click()
    time.sleep(1)
    # Set Speed format to knots
    self.driver.find_element_by_xpath("(//button[@type='button'])[4]").click()
    time.sleep(1)
    self.driver.find_element_by_link_text(speedUnitTypesInText[unitNumber]).click()
    time.sleep(2)
    # Click on Position Tab to check correct speed unit
    self.driver.find_element_by_id("uvms-header-menu-item-movement").click()
    time.sleep(5)
    currentSpeedValue = self.driver.find_element_by_xpath("//*[@id='content']/div[1]/div[3]/div[2]/div/div[2]/div/div[4]/div/div/div/div/span/table/tbody/tr[1]/td[11]").text
    print("Current: " +  currentSpeedValue + " Short Unit: " + speedUnitTypesShort[unitNumber])
    if currentSpeedValue.find(speedUnitTypesShort[unitNumber]) == -1:
        foundCorrectUnit = False
    else:
        foundCorrectUnit = True
    self.assertTrue(foundCorrectUnit)
    time.sleep(5)
    # Shutdown browser
    shutdown_browser(self)



def generate_and_verify_manual_position(self,speedValue,courseValue):
    # Startup browser and login
    startup_browser_and_login_to_unionVMS(self)
    time.sleep(5)
    # Select Positions tab
    self.driver.find_element_by_id("uvms-header-menu-item-movement").click()
    time.sleep(2)
    # Click on New manual report
    self. driver.find_element_by_xpath("//button[@type='submit']").click()
    time.sleep(2)
    # Enter IRCS value
    self.driver.find_element_by_name("ircs").send_keys(ircsValue[0])
    time.sleep(5)
    ##self.driver.find_element_by_css_selector("strong").click()
    ##//time.sleep(2)
    # Get Current Date and time in UTC
    currentUTCValue = datetime.datetime.utcnow()
    earlierPositionTimeValue = currentUTCValue - datetime.timedelta(hours=deltaTimeValue)
    earlierPositionDateTimeValueString = datetime.datetime.strftime(earlierPositionTimeValue, '%Y-%m-%d %H:%M:%S')
    self.driver.find_element_by_id("manual-movement-date-picker").clear()
    self.driver.find_element_by_id("manual-movement-date-picker").send_keys(earlierPositionDateTimeValueString)
    # Enter Position, Speed and Course
    self.driver.find_element_by_name("latitude").clear()
    self.driver.find_element_by_name("latitude").send_keys(lolaPositionValues[0][0][0])
    self.driver.find_element_by_name("longitude").clear()
    self.driver.find_element_by_name("longitude").send_keys(lolaPositionValues[0][0][1])
    self.driver.find_element_by_name("measuredSpeed").send_keys(str(speedValue))
    self.driver.find_element_by_name("course").send_keys(str(courseValue))
    # Click on Save Button
    self.driver.find_element_by_xpath("(//button[@type='submit'])[4]").click()
    time.sleep(5)
    # Click on Confirm button
    self.driver.find_element_by_xpath("(//button[@type='submit'])[4]").click()
    time.sleep(5)
    # Enter IRCS for newly created position
    self.driver.find_element_by_xpath("(//button[@type='button'])[2]").click()
    time.sleep(2)
    self.driver.find_element_by_link_text("Custom").click()
    self.driver.find_element_by_xpath("//input[@type='text']").clear()
    self.driver.find_element_by_xpath("//input[@type='text']").send_keys(ircsValue[0])
    time.sleep(5)
    # Click on search button
    self.driver.find_element_by_xpath("(//button[@type='submit'])[2]").click()
    time.sleep(5)
    # Verifies position data
    self.assertEqual(countryValue, self.driver.find_element_by_css_selector("td[title=\"" + countryValue + "\"]").text)
    self.assertEqual(externalMarkingValue, self.driver.find_element_by_css_selector("td[title=\"" + externalMarkingValue + "\"]").text)
    self.assertEqual(ircsValue[0], self.driver.find_element_by_css_selector("td[title=\"" + ircsValue[0] + "\"]").text)
    self.assertEqual(vesselName[0], self.driver.find_element_by_link_text(vesselName[0]).text)
    # Bug UVMS-3249 self.assertEqual(earlierPositionDateTimeValueString, self.driver.find_element_by_xpath("//*[@id='content']/div[1]/div[3]/div[2]/div/div[2]/div/div[4]/div/div/div/div/span/table/tbody/tr[1]/td[6]").text)
    self.assertEqual(lolaPositionValues[0][0][0], self.driver.find_element_by_css_selector("td[title=\"" + lolaPositionValues[0][0][0] + "\"]").text)
    self.assertEqual(lolaPositionValues[0][0][1], self.driver.find_element_by_css_selector("td[title=\"" + lolaPositionValues[0][0][1] + "\"]").text)
    self.assertEqual("%.2f" % speedValue + " kts", self.driver.find_element_by_css_selector("td[title=\"" + "%.2f" % speedValue + " kts" + "\"]").text)
    self.assertEqual(str(courseValue) + "°", self.driver.find_element_by_css_selector("td[title=\"" + str(courseValue) + "°" + "\"]").text)
    self.assertEqual(sourceValue[1], self.driver.find_element_by_css_selector("td[title=\"" + sourceValue[1] + "\"]").text)
    time.sleep(5)
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
    time.sleep(5)
    # Select Positions tab
    self.driver.find_element_by_id("uvms-header-menu-item-movement").click()
    time.sleep(7)
    # Enter IRCS for newly created position
    self.driver.find_element_by_xpath("(//button[@type='button'])[2]").click()
    self.driver.find_element_by_link_text("Custom").click()
    self.driver.find_element_by_xpath("//input[@type='text']").clear()
    self.driver.find_element_by_xpath("//input[@type='text']").send_keys(ircsValue[0])
    time.sleep(5)
    # Click on search button
    self.driver.find_element_by_xpath("(//button[@type='submit'])[2]").click()
    time.sleep(5)
    # Enter Vessel to verify position data
    self.assertEqual(countryValue, self.driver.find_element_by_css_selector("td[title=\"" + countryValue + "\"]").text)
    self.assertEqual(externalMarkingValue,
                     self.driver.find_element_by_css_selector("td[title=\"" + externalMarkingValue + "\"]").text)
    self.assertEqual(ircsValue[0], self.driver.find_element_by_css_selector("td[title=\"" + ircsValue[0] + "\"]").text)
    self.assertEqual(vesselName[0], self.driver.find_element_by_link_text(vesselName[0]).text)
    # Bug UVMS-3249 self.assertEqual(earlierPositionDateTimeValueString, self.driver.find_element_by_xpath("//*[@id='content']/div[1]/div[3]/div[2]/div/div[2]/div/div[4]/div/div/div/div/span/table/tbody/tr[1]/td[6]").text)
    self.assertEqual(lolaPositionValues[0][0][0], self.driver.find_element_by_css_selector("td[title=\"" + lolaPositionValues[0][0][0] + "\"]").text)
    self.assertEqual(lolaPositionValues[0][0][1], self.driver.find_element_by_css_selector("td[title=\"" + lolaPositionValues[0][0][1] + "\"]").text)
    self.assertEqual("%.2f" % speedValue + " kts", self.driver.find_element_by_css_selector("td[title=\"" + "%.2f" % speedValue + " kts" + "\"]").text)
    self.assertEqual(str(courseValue) + "°", self.driver.find_element_by_css_selector("td[title=\"" + str(courseValue) + "°" + "\"]").text)
    self.assertEqual(sourceValue[0], self.driver.find_element_by_css_selector("td[title=\"" + sourceValue[0] + "\"]").text)
    time.sleep(5)
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
        time.sleep(5)
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
        time.sleep(5)
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
        time.sleep(5)
        create_one_new_asset_from_gui(self, ircsValue,vesselName,externalMarkingValue,cfrValue,imoValue,homeportValue,mmsiValue,lengthValue,grossTonnageValue,powerValue,producernameValue,producercodeValue,contactNameValue,contactEmailValue,contactPhoneNumberValue)
        check_new_asset_exists(self,countryValue,gearTypeValue,licenseTypeValue, ircsValue,vesselName,externalMarkingValue,cfrValue,imoValue,homeportValue,mmsiValue,lengthValue,grossTonnageValue,powerValue,producernameValue,producercodeValue,contactNameValue,contactEmailValue,contactPhoneNumberValue)

        populateIridiumImarsatCData()

        create_one_new_mobile_terminal_from_gui(self, serialNoValue, transceiverType, softwareVersion, antennaVersion, satelliteNumber, dnidNumber, memberIdnumber, installedByName, expectedFrequencyHours, gracePeriodFrequencyHours, inPortFrequencyHours)
        check_new_mobile_terminal_exists(self, serialNoValue, memberIdnumber, dnidNumber, transceiverType, softwareVersion, satelliteNumber, antennaVersion, installedByName)

        populateIridiumImarsatCData()
         
        link_asset_and_mobile_terminal(self,serialNoValue, ircsValue)
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
