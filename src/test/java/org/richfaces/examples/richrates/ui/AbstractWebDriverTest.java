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
package org.richfaces.examples.richrates.ui;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.test.selenium.listener.ConsoleStatusTestListener;
import org.jboss.test.selenium.pagefactory.StaleReferenceAwareFieldDecorator;
import org.jboss.test.selenium.utils.testng.TestInfo;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.android.AndroidDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners(ConsoleStatusTestListener.class)
public abstract class AbstractWebDriverTest<P extends Page> extends Arquillian {

    @Drone
    public WebDriver driver;
    @ArquillianResource
    private URL deployedRoot;
    private P page;
    protected File mavenProjectBuildDirectory = new File(System.getProperty("maven.project.build.directory",
        "./target/"));
    protected File failuresOutputDir = new File(mavenProjectBuildDirectory, "failures");

    // TODO include manifest.mf with org.slf4j dependency
    @Deployment(testable = false)
    public static WebArchive createTestArchive() {
        MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom(
            "pom.xml");

        WebArchive war = ShrinkWrap.create(WebArchive.class, "richrates.war");

        war.addPackage("org.richfaces.examples.richrates");
        war.addPackage("org.richfaces.examples.richrates.annotation");
        war.addPackages(true, "org.slf4j");

        war.merge(ShrinkWrap.create(ExplodedImporter.class, "tmp1.war").importDirectory("src/main/webapp")
            .as(WebArchive.class));
        war.merge(
            ShrinkWrap.create(ExplodedImporter.class, "tmp2.war").importDirectory("src/main/resources")
                .as(WebArchive.class), "WEB-INF/classes");

        war.addAsLibraries(resolver.artifact("org.richfaces.ui:richfaces-components-ui").resolveAsFiles());
        war.addAsLibraries(resolver.artifact("org.richfaces.core:richfaces-core-impl").resolveAsFiles());
        war.addAsLibraries(resolver.artifact("org.jdom:jdom").resolveAsFiles());
        war.addAsLibraries(resolver.artifact("joda-time:joda-time").resolveAsFiles());

        return war;
    }

    @BeforeMethod(dependsOnGroups = { "arquillian" })
    public void preparePage() throws MalformedURLException {

        page = createPage(getRoot());
        if (driver instanceof AndroidDriver) {
            driver.get(getPage().getUrl().toString().replace("faces", "faces/mobile"));
        } else {
            driver.get(getPage().getUrl().toString());
        }
        FieldDecorator decoraor = new StaleReferenceAwareFieldDecorator(new DefaultElementLocatorFactory(driver), 5);
        PageFactory.initElements(decoraor, page);
    }

    protected P getPage() {
        return page;
    }

    protected WebDriver getWebDriver() {
        return driver;
    }

    private URL getRoot() throws MalformedURLException {
        if (System.getProperty("server.address") != null) {
            return new URL(deployedRoot.toString()
                .replace(deployedRoot.getHost(), System.getProperty("server.address")));
        } else if (driver instanceof AndroidDriver) {
            return new URL(deployedRoot.toString().replace(deployedRoot.getHost(), "10.0.2.2"));
        } else {
            return deployedRoot;
        }
    }

    protected abstract P createPage(URL root);

    @AfterMethod(alwaysRun = true, groups = "arquillian")
    public void handleTestError(ITestResult result) {
        if (driver == null) {
            return;
        }

        if (result.getStatus() == ITestResult.SUCCESS) {
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

    private String getFilenameIdentification(ITestResult result) {
        return TestInfo.getClassMethodName(result);
    }

}
