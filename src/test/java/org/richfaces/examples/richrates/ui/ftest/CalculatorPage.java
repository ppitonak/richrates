/*******************************************************************************
 * JBoss, Home of Professional Open Source
 * Copyright 2010-2013, Red Hat, Inc. and individual contributors
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

import java.util.List;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.GrapheneElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.richfaces.examples.richrates.ui.AbstractPage;

/**
 * @author <a href="mailto:jpapouse@redhat.com">Jan Papousek</a>
 * @author <a href="https://community.jboss.org/people/ppitonak">Pavol Pitonak</a>
 */
public class CalculatorPage extends AbstractPage {

    @FindBy(id = "calculator:amount")
    private WebElement amountInput;
    @FindBy(css = "div.result")
    private WebElement resultOutput;
    @FindBy(id = "calculator:calculateButton")
    private WebElement submitButton;
    @FindBy(css = "img[id$=flag]")
    private List<GrapheneElement> flags;
    @FindBy(id = "calculator:swap")
    private WebElement swapButton;
    

    public String getResult() {
        return resultOutput.getText();
    }

    public void setAmount(int amount) {
        amountInput.click();
        amountInput.clear();
        amountInput.sendKeys(String.valueOf(amount));
        submitButton.click();
    }

    public void waitUntilResultOutputIsNotEmpty() {
        Graphene.waitAjax().until().element(resultOutput).text().not().equalTo("");
    }

    public String getURL() {
        return "faces/calculator.xhtml";
    }
    
    public void selectCurrency(String currencyCode) {
        WebElement flag = null;
        for (WebElement element : flags) {
            if (element.getAttribute("alt").contains(currencyCode)) {
                flag = element;
                break;
            }
        }
        Graphene.guardAjax(flag).click();
    }
    
    public void swapCurrencies() {
        Graphene.guardAjax(swapButton).click();
    }
}
