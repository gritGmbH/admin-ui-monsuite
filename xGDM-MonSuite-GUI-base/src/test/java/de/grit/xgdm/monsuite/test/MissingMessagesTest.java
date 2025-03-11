/*-
 * #%L
 * xGDM-MonSuite GUI (Base)
 * %%
 * Copyright (C) 2022 - 2025 grit GmbH
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package de.grit.xgdm.monsuite.test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.grit.vaadin.common.Messages;
import de.grit.vaadin.common.tests.AbstractValidateFirstExpressionTest;

@RunWith(Parameterized.class)
public class MissingMessagesTest extends AbstractValidateFirstExpressionTest {

    private static Path searchDir = Paths.get( "src/main/java" );

    @Parameters(name = "{index}: {0}")
    public static List<Path> getFilesToTest()
                            throws IOException {
        return getFilesMatching( searchDir, "glob:*.java" );
    }

    @Parameter(0)
    public Path testSourceFile;

    @Override
    public Path getBasePath() {
        return searchDir;
    }

    @Override
    public Path getFileToTest() {
        return testSourceFile;
    }

    public boolean validate( String key ) {
        String failedKey = '!' + key + '!';
        return !failedKey.equals( Messages.get( key ) );
    }
}
