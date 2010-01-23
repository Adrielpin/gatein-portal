package org.exoplatform.portal.selenium;

import com.thoughtworks.selenium.*;
import java.util.regex.Pattern;
public class Test_SNF_PRL_03 extends SeleneseTestCase {
public String speed = "1000";
public String browser = "firefox";
public void setSpeed() {
selenium.setSpeed(speed);
}

public void setUp() throws Exception {
setUp("http://localhost:8080/portal/", "*" + browser);
}

public void testSNF_PRL_03() throws Exception {
setSpeed();
selenium.open("/portal/public/classic/");
System.out.println("-Change Language-");
selenium.clickAt("//a[@class='Language']", "1,1");
for (int second = 0;; second++) {
if (second >= 30) fail("timeout");
try {
 if (selenium.isElementPresent("link=Vietnamese")) 
break; }
 catch (Exception e) {}
Thread.sleep(1000);
}
selenium.clickAt("link=Vietnamese", "1,1");
selenium.clickAt("link=Apply", "1,1");
for (int second = 0;; second++) {
if (second >= 30) fail("timeout");
try {
if (!selenium.isElementPresent("link=Apply"))
 break;
 }
 catch (Exception e) {}
Thread.sleep(1000);
}
selenium.clickAt("//a[@class='Language']", "1,1");
for (int second = 0;; second++) {
if (second >= 30) fail("timeout");
try {
 if (selenium.isElementPresent("//div[@id='UITabContent']//div[2]/a")) 
break; }
 catch (Exception e) {}
Thread.sleep(1000);
}
selenium.clickAt("//div[@id='UITabContent']//div[2]/a", "1,1");
selenium.click("link=�p d?ng");
selenium.waitForPageToLoad("30000");
assertTrue(selenium.isElementPresent("link=Sign in"));
}

}
