package org.python.pydev.debug.ui.blocks;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.python.pydev.core.IInterpreterManager;
import org.python.pydev.debug.core.Constants;
import org.python.pydev.debug.ui.launching.PythonRunnerConfig;
import org.python.pydev.plugin.PydevPlugin;
import org.python.pydev.runners.SimpleRunner;

/**
 * A control for displaying a list of python paths.
 */
public class PythonPathBlock extends AbstractLaunchConfigurationTab {

    private List fPythonPathList;

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setText("PYTHONPATH that will be used in the run:");
        fPythonPathList = new List(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);

        GridData gd = new GridData(GridData.FILL_BOTH);
        fPythonPathList.setLayoutData(gd);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
     */
    public String getName() {
        return "Python path";
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
     */
    public void initializeFrom(ILaunchConfiguration configuration) {

        try {
            String id = configuration.getType().getIdentifier();
            IInterpreterManager manager;
            if(Constants.ID_JYTHON_LAUNCH_CONFIGURATION_TYPE.equals(id) || Constants.ID_JYTHON_UNITTEST_LAUNCH_CONFIGURATION_TYPE.equals(id)){
                manager = PydevPlugin.getJythonInterpreterManager();
            }else{
                manager = PydevPlugin.getPythonInterpreterManager();
            }
            String pythonPath = PythonRunnerConfig.getPythonpathFromConfiguration(configuration, manager);

            fPythonPathList.removeAll();
            java.util.List<String> paths = SimpleRunner.splitPythonpath(pythonPath);
            for (String p:paths) {
                fPythonPathList.add(p);
            }
        } catch (Exception e) {
            // Exceptions here may have several reasons
            // - The interpreter is incorrectly configured
            // - The arguments use an unresolved variable.
            // In each case, the exception contains a meaningful message, that is displayed
            fPythonPathList.removeAll();
            fPythonPathList.add(e.getMessage());
            PydevPlugin.log(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        // Nothing to apply, this is a read-only control
        initializeFrom(configuration);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        // No defaults to set
    }
}