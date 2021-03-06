/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.graalvm.visualvm.lib.jfluid.wireprotocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * This is issued by back end in response to GetMethodNamesForJMethodIdsCommand. It contains strings with methods'
 * classes, names and signatures, packed into a single byte[] array. At the client side this data is subsequently
 * unpacked (not in this class to avoid having unused code at the back end side).
 *
 * @author Misha Dmitriev
 * @author Ian Formanek
 */
public class MethodNamesResponse extends Response {
    //~ Instance fields ----------------------------------------------------------------------------------------------------------

    private int[] packedArrayOffsets;
    private byte[] packedData;

    //~ Constructors -------------------------------------------------------------------------------------------------------------

    public MethodNamesResponse(byte[] packedData, int[] packedArrayOffsets) {
        super(true, METHOD_NAMES);
        this.packedData = packedData;
        this.packedArrayOffsets = packedArrayOffsets;
    }

    // Custom serialization support
    MethodNamesResponse() {
        super(true, METHOD_NAMES);
    }

    //~ Methods ------------------------------------------------------------------------------------------------------------------

    public int[] getPackedArrayOffsets() {
        return packedArrayOffsets;
    }

    public byte[] getPackedData() {
        return packedData;
    }

    void readObject(ObjectInputStream in) throws IOException {
        int len = in.readInt();
        packedData = new byte[len];
        in.readFully(packedData);
        len = in.readInt();
        packedArrayOffsets = new int[len];

        for (int i = 0; i < len; i++) {
            packedArrayOffsets[i] = in.readInt();
        }
    }

    void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(packedData.length);
        out.write(packedData);
        out.writeInt(packedArrayOffsets.length);

        for (int i = 0; i < packedArrayOffsets.length; i++) {
            out.writeInt(packedArrayOffsets[i]);
        }

        packedData = null;
        packedArrayOffsets = null;
    }
}
