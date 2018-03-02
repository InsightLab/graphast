package org.insightlab.graphast.exceptions;

import org.insightlab.graphast.model.components.Component;

public class ComponentNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -915981947597562490L;
	
	public ComponentNotFoundException() {
		super();
	}
	
	public ComponentNotFoundException(Class<? extends Component> component) {
		this(component.getName());
	}
	
	public ComponentNotFoundException(String componentName) {
		super("Component '" + componentName + "' not found!");
	}

}
