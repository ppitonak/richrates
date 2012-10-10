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

import org.openqa.selenium.support.ui.WebDriverWait;
import org.richfaces.examples.richrates.ui.AbstractWebDriverTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="https://community.jboss.org/people/ppitonak">Pavol Pitonak</a>
 * @since 4.2.0
 */
public class CalculatorTest extends AbstractWebDriverTest<CalculatorPage> {

    @Test
    public void testCalculate() {
        page.setAmount(33);
        page.waitUntilResultOutputIsNotEmpty(new WebDriverWait(getWebDriver(), 5));
        
        String result = page.getResult();

        Assert.assertTrue(result.matches("33.000 EUR = \\d+\\.\\d{3} USD"),
            "Result should start match expression \"33.000 EUR = \\d+\\.\\d{3} USD\".");
        Assert.assertNotEquals(result, "33.000 EUR = 0.000 USD", "Exchange rate for USD should not be 0.");
    }
}
