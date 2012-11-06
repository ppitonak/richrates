/*******************************************************************************
 * JBoss, Home of Professional Open Source
 * Copyright 2009-2010, Red Hat, Inc. and individual contributors
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
package org.jboss.test.selenium.listener;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jboss.arquillian.graphene.context.GrapheneContext;
import org.jboss.test.selenium.utils.testng.TestInfo;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 * Test listener which provides the methods injected in lifecycle of test case to catch the additional information in
 * context of test failure.
 *
 * @author <a href="https://community.jboss.org/people/ppitonak">Pavol Pitonak</a>
 * @since 4.3.0.M3
 */
public class FailureLoggingTestListener extends TestListenerAdapter {

    private WebDriver driver = GrapheneContext.getProxyForInterfaces(TakesScreenshot.class);
    private File mavenProjectBuildDirectory = new File(System.getProperty("maven.project.build.directory", "./target/"));
    private File failuresOutputDir = new File(mavenProjectBuildDirectory, "failures");

    @Override
    public void onTestStart(ITestResult result) {
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (driver == null) {
            return;
        }

        Throwable throwable = result.getThrowable();
        String stacktrace = null;

        if (throwable != null) {
            stacktrace = ExceptionUtils.getStackTrace(throwable);
        }

        String filenameIdentification = getFilenameIdentification(result);

        String htmlSource = driver.getPageSource();

        File stacktraceOutputFile = new File(failuresOutputDir, filenameIdentification + "/stacktrace.txt");
        File imageOutputFile = new File(failuresOutputDir, filenameIdentification + "/screenshot.png");
        File htmlSourceOutputFile = new File(failuresOutputDir, filenameIdentification + "/html-source.html");

        try {
            File directory = imageOutputFile.getParentFile();
            FileUtils.forceMkdir(directory);

            FileUtils.writeStringToFile(stacktraceOutputFile, stacktrace);
            FileUtils.writeStringToFile(htmlSourceOutputFile, htmlSource);

            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, imageOutputFile);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
    }

    @Override
    public void onTestSuccess(ITestResult result) {
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    private String getFilenameIdentification(ITestResult result) {
        return TestInfo.getClassMethodName(result);
    }
}
