
/*
 * Janino - An embedded Java[TM] compiler
 *
 * Copyright (c) 2001-2010 Arno Unkrig. All rights reserved.
 * Copyright (c) 2015-2016 TIBCO Software Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

// SUPPRESS CHECKSTYLE JavadocMethod:9999

package org.codehaus.commons.compiler.tests;

import java.io.File;
import java.util.Collection;

import org.codehaus.commons.compiler.AbstractJavaSourceClassLoader;
import org.codehaus.commons.compiler.ICompilerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import util.TestUtil;

/**
 * Tests for the {@link IJavaSourceClassLoader}.
 */
@RunWith(Parameterized.class) public
class JavaSourceClassLoaderTest {

    private static final File[] SOURCE_PATH = {
        new File("../janino/src/main/java"),
        new File("../commons-compiler/src/main/java"),
    };

    private final ICompilerFactory compilerFactory;

    @Parameters(name = "CompilerFactory={0}") public static Collection<Object[]>
    compilerFactories() throws Exception {
        return TestUtil.getCompilerFactoriesForParameters();
    }

    public
    JavaSourceClassLoaderTest(ICompilerFactory compilerFactory) {
        this.compilerFactory = compilerFactory;
    }

    @Test public void
    testJavaSourceClassLoader() throws Exception {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        systemClassLoader.loadClass(this.getClass().getName());

        ClassLoader extensionsClassLoader = systemClassLoader.getParent();
        try {
            extensionsClassLoader.loadClass(this.getClass().getName());
            Assert.fail("extensionsClassLoader should be separate from the current classloader");
        } catch (ClassNotFoundException cnfe) {
            // as we had intended
        }
        extensionsClassLoader.loadClass("java.lang.String");
        AbstractJavaSourceClassLoader jscl = this.compilerFactory.newJavaSourceClassLoader(extensionsClassLoader);
        jscl.setSourcePath(JavaSourceClassLoaderTest.SOURCE_PATH);
        jscl.loadClass("org.codehaus.janino.Compiler");
    }

    @Test public void
    testCircularSingleTypeImports() throws Exception {
        AbstractJavaSourceClassLoader jscl = this.compilerFactory.newJavaSourceClassLoader(
            ClassLoader.getSystemClassLoader().getParent()
        );
        jscl.setSourcePath(new File[] { new File("src/test/resources/testCircularSingleTypeImports/") });
        jscl.loadClass("test.Func1");
    }

    @Test public void
    testCircularStaticImports() throws Exception {
        AbstractJavaSourceClassLoader jscl = this.compilerFactory.newJavaSourceClassLoader(
            ClassLoader.getSystemClassLoader().getParent()
        );
        jscl.setSourcePath(new File[] { new File("src/test/resources/testCircularStaticImports/") });
        jscl.loadClass("test.Func1");
    }
}
