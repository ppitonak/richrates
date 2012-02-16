/*******************************************************************************
 * JBoss, Home of Professional Open Source
 * Copyright 2010-2012, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *******************************************************************************/
package org.richfaces.examples.richrates.ui.ftest;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.richfaces.examples.richrates.ui.AbstractPage;
import org.richfaces.examples.richrates.ui.TextNotEquals;

/**
 * @author <a href="mailto:jpapouse@redhat.com">Jan Papousek</a>
 */
public class CalculatorPage extends AbstractPage {

    @FindBy(id = "calculator:amount")
    private WebElement amountInput;
    @FindBy(xpath = "//div[contains(@class, 'result')]")
    private WebElement resultOutput;
    @FindBy(id = "calculator:calculateButton")
    private WebElement submitButton;

    public CalculatorPage(URL root) {
        super(root);
    }

    public String getResult() {
        return resultOutput.getText();
    }

    public void setAmount(int amount) {
        amountInput.click();
        amountInput.clear();
        amountInput.sendKeys(String.valueOf(amount));
        submitButton.click();
    }

    public void waitUntilResultOutputIsNotEmpty(WebDriverWait webDriverWait) {
        webDriverWait.until(new TextNotEquals(resultOutput, ""));
    }

    protected URL createUrl(URL root) {
        try {
            return new URL(root, "faces/calculator.xhtml");
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

}
