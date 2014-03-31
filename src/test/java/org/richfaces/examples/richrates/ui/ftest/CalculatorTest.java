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

import org.jboss.arquillian.graphene.page.Page;
import org.richfaces.examples.richrates.ui.AbstractWebDriverTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author <a href="https://community.jboss.org/people/ppitonak">Pavol Pitonak</a>
 * @since 4.2.0
 */
public class CalculatorTest extends AbstractWebDriverTest {

    @Page
    private CalculatorPage page;
    
    @BeforeMethod(dependsOnGroups = "arquillian")
    private void openPage() {
        loadPage("faces/calculator.xhtml");
    }
    
    @Test
    public void testCalculateEurToUsd() {
        page.selectCurrency("USD");
        page.setAmount(33);
        page.waitUntilResultOutputIsNotEmpty();
        
        String result = page.getResult();

        Assert.assertTrue(result.matches("33.000 EUR = \\d+\\.\\d{3} USD"),
            "Result should start match expression \"33.000 EUR = \\d+\\.\\d{3} USD\".");
        Assert.assertNotEquals(result, "33.000 EUR = 0.000 USD", "Exchange rate for USD should not be 0.");
    }
    
    @Test
    public void testChangeCurrency() {
        page.selectCurrency("CZK");
        page.setAmount(33);
        page.waitUntilResultOutputIsNotEmpty();
        
        String result = page.getResult();

        Assert.assertTrue(result.matches("33.000 EUR = \\d+\\.\\d{3} CZK"),
            "Result should match expression \"33.000 EUR = \\d+\\.\\d{3} CZK\".");
        Assert.assertNotEquals(result, "33.000 EUR = 0.000 USD", "Exchange rate for CZK should not be 0.");
    }
    
    @Test
    public void testSwapCurrencies() {
        page.selectCurrency("TRY");
        page.setAmount(33);
        page.waitUntilResultOutputIsNotEmpty();
        
        String firstResult = page.getResult();

        Assert.assertTrue(firstResult.matches("33.000 EUR = \\d+\\.\\d{3} TRY"),
            "Result should match expression \"33.000 EUR = \\d+\\.\\d{3} TRY\".");
        Assert.assertNotEquals(firstResult, "33.000 EUR = 0.000 TRY", "Exchange rate for TRY should not be 0.");
        
        page.swapCurrencies();
        
        String secondResult = page.getResult();

        Assert.assertTrue(secondResult.matches("33.000 TRY = \\d+\\.\\d{3} EUR"),
            "Result should match expression \"33.000 TRY = \\d+\\.\\d{3} EUR\".");
        Assert.assertNotEquals(secondResult, "33.000 TRY = 0.000 EUR", "Exchange rate for TRY should not be 0.");
        Assert.assertNotEquals(firstResult, secondResult, "Exchange rate for EUR to TRY cannot be the same as TRY to EUR.");
    }
}
