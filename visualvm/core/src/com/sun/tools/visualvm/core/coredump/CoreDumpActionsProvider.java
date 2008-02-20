/*
 * Copyright 2007-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package com.sun.tools.visualvm.core.coredump;

import com.sun.tools.visualvm.core.datasource.CoreDump;
import com.sun.tools.visualvm.core.datasource.DataSource;
import com.sun.tools.visualvm.core.datasource.DataSourceRoot;
import com.sun.tools.visualvm.core.explorer.ExplorerActionDescriptor;
import com.sun.tools.visualvm.core.explorer.ExplorerActionsProvider;
import com.sun.tools.visualvm.core.explorer.ExplorerContextMenuFactory;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;

/**
 *
 * @author Tomas Hurka
 */
class CoreDumpActionsProvider {
    
    private static final AddNewCoreDumpAction addNewCoreDumpAction = new AddNewCoreDumpAction();
    
    
    static void register() {
        ExplorerContextMenuFactory explorer = ExplorerContextMenuFactory.sharedInstance();
        explorer.addExplorerActionsProvider(new CoreDumpActionProvider(), CoreDump.class);
        explorer.addExplorerActionsProvider(new CoreDumpsContainerActionProvider(), CoreDumpsContainer.class);
        explorer.addExplorerActionsProvider(new DataSourceRootActionProvider(), DataSourceRoot.class);
    }
    
    
    private static class AddNewCoreDumpAction extends AbstractAction {
        
        public AddNewCoreDumpAction() {
            super("Add VM Coredump...");
        }
        
        public void actionPerformed(ActionEvent e) {
            CoreDumpConfigurator newCoreDumpConfiguration = CoreDumpConfigurator.defineCoreDump();
            if (newCoreDumpConfiguration != null) {
                CoreDumpProvider provider = CoreDumpProvider.sharedInstance();
                provider.createCoreDump(newCoreDumpConfiguration.getCoreDumpFile(),newCoreDumpConfiguration.getDisplayname(), newCoreDumpConfiguration.getJavaHome());
            }
        }
    }
    
    private static class RenameCoreDumpAction extends AbstractAction {
        
        private CoreDump coreDump;
        
        public RenameCoreDumpAction(CoreDump dmp) {
            super("Rename...");
            coreDump = dmp;
        }
        
        public void actionPerformed(ActionEvent e) {
            CoreDumpConfigurator newCoreDumpConfiguration = CoreDumpConfigurator.renameCoreDump(coreDump);
            if (newCoreDumpConfiguration != null) {
                coreDump.setDisplayName(newCoreDumpConfiguration.getDisplayname());
            }
        }
        
    }
    
    private static class RemoveCoreDumpAction extends AbstractAction {
        
        private CoreDumpImpl coreDump;
        
        public RemoveCoreDumpAction(CoreDumpImpl dmp) {
            super("Remove");
            this.coreDump = dmp;
        }
        
        public void actionPerformed(ActionEvent e) {
            CoreDumpProvider.sharedInstance().removeCoreDump(coreDump, true);
        }
        
    }
    
    private static abstract class AbstractCoreDumpActionProvider<T extends DataSource> implements ExplorerActionsProvider<T> {
        
        public ExplorerActionDescriptor getDefaultAction(T dataSource) {
            return null;
        }
        
        public Set<ExplorerActionDescriptor> getActions(T coreDump) {
            return Collections.EMPTY_SET;
        }
        
    }
    
    private static class CoreDumpActionProvider extends AbstractCoreDumpActionProvider<CoreDump> {
        
        public Set<ExplorerActionDescriptor> getActions(CoreDump coreDump) {
            Set<ExplorerActionDescriptor> actions = new HashSet();
            
            actions.add(new ExplorerActionDescriptor(null, 30));
            actions.add(new ExplorerActionDescriptor(new RenameCoreDumpAction(coreDump), 40));
            if (coreDump instanceof CoreDumpImpl) actions.add(new ExplorerActionDescriptor(new RemoveCoreDumpAction((CoreDumpImpl)coreDump), 50));
            
            return actions;
        }
    }
    
    private static class CoreDumpsContainerActionProvider extends AbstractCoreDumpActionProvider<CoreDumpsContainer> {
        
        public ExplorerActionDescriptor getDefaultAction(CoreDumpsContainer dataSource) {
            return new ExplorerActionDescriptor(addNewCoreDumpAction, 0);
        }
        
    }
    
    private static class DataSourceRootActionProvider extends AbstractCoreDumpActionProvider<DataSource> {
        
        public Set<ExplorerActionDescriptor> getActions(DataSource root) {
            Set<ExplorerActionDescriptor> actions = new HashSet();
            
            actions.add(new ExplorerActionDescriptor(addNewCoreDumpAction, 20));
            
            return actions;
        }
        
    }
    
}
