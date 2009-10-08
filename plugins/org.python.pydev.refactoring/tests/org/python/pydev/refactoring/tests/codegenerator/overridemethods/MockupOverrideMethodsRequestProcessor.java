/* 
 * Copyright (C) 2006, 2007  Dennis Hunziker, Ueli Kistler 
 */

package org.python.pydev.refactoring.tests.codegenerator.overridemethods;

import java.util.ArrayList;
import java.util.List;

import org.python.pydev.core.IGrammarVersionProvider;
import org.python.pydev.core.MisconfigurationException;
import org.python.pydev.refactoring.ast.adapters.AdapterPrefs;
import org.python.pydev.refactoring.ast.adapters.ClassDefAdapter;
import org.python.pydev.refactoring.ast.adapters.FunctionDefAdapter;
import org.python.pydev.refactoring.ast.adapters.ModuleAdapter;
import org.python.pydev.refactoring.codegenerator.overridemethods.request.OverrideMethodsRequest;
import org.python.pydev.refactoring.core.request.IRequestProcessor;

public class MockupOverrideMethodsRequestProcessor implements IRequestProcessor<OverrideMethodsRequest> {

	private ModuleAdapter module;

	private int classSelection;

	private int offsetStrategy;

	private List<Integer> methodSelection;

	private int editClass;

	private MockupOverrideMethodsRequestProcessor(ModuleAdapter module, int classSelection, List<Integer> methodSelection,
			int offsetStrategy, int editClass) {
		this.module = module;
		this.methodSelection = methodSelection;
		this.classSelection = classSelection;
		this.offsetStrategy = offsetStrategy;
		this.editClass = editClass;
	}

	public MockupOverrideMethodsRequestProcessor(ModuleAdapter module, MockupOverrideMethodsConfig config) {
		this(module, config.getClassSelection(), config.getMethodSelection(), config.getOffsetStrategy(), config.getEditClass());
	}

	public List<OverrideMethodsRequest> getRefactoringRequests() {
		ClassDefAdapter clazz = (ClassDefAdapter) module.getClasses().get(editClass);
		ClassDefAdapter clazzSelection = (ClassDefAdapter) module.getClasses().get(classSelection);
		String baseClassName = module.getClasses().get(classSelection).getName();
		List<FunctionDefAdapter> methods = new ArrayList<FunctionDefAdapter>();

		for (int index : methodSelection) {
			methods.add(clazzSelection.getFunctions().get(index));
		}

		List<OverrideMethodsRequest> requests = new ArrayList<OverrideMethodsRequest>();

		for (FunctionDefAdapter method : methods) {
			OverrideMethodsRequest req = new OverrideMethodsRequest(
			        clazz, this.offsetStrategy, method, false, baseClassName, new AdapterPrefs("\n", new IGrammarVersionProvider() {
                        
                        @Override
                        public int getGrammarVersion() throws MisconfigurationException {
                            return IGrammarVersionProvider.GRAMMAR_PYTHON_VERSION_2_6;
                        }
                    }));
			requests.add(req);
		}

		return requests;

	}
}
