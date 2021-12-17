/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.docker.validation.frontend.pages;

import static com.codeborne.selenide.Selenide.localStorage;
import static com.codeborne.selenide.Selenide.open;
import java.util.UUID;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import com.codeborne.selenide.Configuration;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

public class UnionVMS extends AbstractRest {

    private UnionVMS() {
        Configuration.baseUrl = "http://localhost:28080";
        Configuration.headless = true;
        if (System.getProperty("os.name").equals("Linux")) {
            /* 
               Install google-chrome
              
                1: wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
                
                2: sudo dpkg -i google-chrome-stable_current_amd64.deb
                
                If you see errors about any missing dependencies you can force install the missing parts:
                
                sudo apt -f install
            */
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            Configuration.browserCapabilities = new DesiredCapabilities();
            Configuration.browserCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
        }
        String token = getValidJwtToken();
        open("/");
        localStorage().setItem("authToken", token);
    }

    public static UnionVMS login() {
        return new UnionVMS();
    }

    public RealtimeMapPage realtimeMapPage() {
        return new RealtimeMapPage();
    }

    public AssetSearchPage assetSearchPage() {
        return new AssetSearchPage();
    }

    public AssetPage assetPage(UUID assetId) {
        return new AssetPage(assetId);
    }
}
