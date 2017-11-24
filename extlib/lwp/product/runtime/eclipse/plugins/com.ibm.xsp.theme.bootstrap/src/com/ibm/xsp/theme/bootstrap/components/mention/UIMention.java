package com.ibm.xsp.theme.bootstrap.components.mention;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.xsp.component.FacesAjaxComponent;
import com.ibm.xsp.component.UIOutputEx;
import com.ibm.xsp.extlib.component.rest.DominoViewJsonService;
import com.ibm.xsp.extlib.component.rest.IRestService;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.webapp.XspHttpServletResponse;

public class UIMention extends UIOutputEx implements FacesAjaxComponent {

	private String _forId;

	// If we are supplying data via Rest Service URL
	private String restUrl;

	// If we are supplying data via Rest Server using Pagename and Pathinfo
	// combination
	private String pageName;
	private String pathInfo;

	// If We want to make this control act as a Rest Server
	private String databaseName;
	private String viewName;
	private String userNameColumnName;
	private String nameColumnName;
	private String imageColumnName;

	public static final String RENDERER_TYPE = "com.ibm.xsp.theme.bootstrap.Mention";

	public UIMention() {
		setRendererType(RENDERER_TYPE);
	}

	public String getFor() {

		if (this._forId != null) {
			return this._forId;
		}

		ValueBinding vb = getValueBinding("for");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setFor(String _forId) {
		this._forId = _forId;
	}

	public String getRestUrl() {

		if (this.restUrl != null) {
			return this.restUrl;
		}

		ValueBinding vb = getValueBinding("url");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setRestUrl(String url) {
		this.restUrl = url;
	}

	public String getPageName() {

		if (this.pageName != null) {
			return this.pageName;
		}

		ValueBinding vb = getValueBinding("pageName");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getPathInfo() {

		if (this.pathInfo != null) {
			return this.pathInfo;
		}

		ValueBinding vb = getValueBinding("pathInfo");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}

	public String getDatabaseName() {

		if (this.databaseName != null) {
			return this.databaseName;
		}

		ValueBinding vb = getValueBinding("databaseName");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getViewName() {

		if (this.viewName != null) {
			return this.viewName;
		}

		ValueBinding vb = getValueBinding("viewName");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setViewName(String _viewName) {
		this.viewName = _viewName;
	}

	public String getUserNameColumnName() {

		if (this.userNameColumnName != null) {
			return this.userNameColumnName;
		}

		ValueBinding vb = getValueBinding("userNameColumnName");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setUserNameColumnName(String _userNameColumnName) {
		this.userNameColumnName = _userNameColumnName;
	}

	public String getNameColumnName() {

		if (this.nameColumnName != null) {
			return this.nameColumnName;
		}

		ValueBinding vb = getValueBinding("nameColumnName");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setNameColumnName(String _nameColumnName) {
		this.nameColumnName = _nameColumnName;
	}

	public String getImageColumnName() {

		if (this.imageColumnName != null) {
			return this.imageColumnName;
		}

		ValueBinding vb = getValueBinding("imageColumnName");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setImageColumnName(String _imageColumnName) {
		this.imageColumnName = _imageColumnName;
	}

	public String getEffectiveRestUrl() {

		if (StringUtil.isNotEmpty(getRestUrl())) {
			return getRestUrl();
		}

		if (StringUtil.isNotEmpty(getPageName()) && StringUtil.isNotEmpty(getPathInfo())) {

			return getPageName() + "/" + getPathInfo();
		}

		if (StringUtil.isNotEmpty(getViewName())) {

			String currentPage = "something";
			return currentPage + "/" + getPathInfoAutomaticRest();
		}

		return null;

	}

	public String getPathInfoAutomaticRest() {
		// Possible Clash Here
		return getId();
	}

	@Override
	public boolean handles(FacesContext context) {

		String reqPathInfo = context.getExternalContext().getRequestPathInfo();

		if (StringUtil.isNotEmpty(reqPathInfo)) {

			String pathInfo = getPathInfoAutomaticRest();

			if (StringUtil.isEmpty(pathInfo)) {
				return false;
			}
			if (!pathInfo.startsWith("/")) {
				pathInfo = "/" + pathInfo;
			}
			if (!pathInfo.endsWith("/")) {
				pathInfo += "/";
			}
			if (!reqPathInfo.startsWith("/")) {
				reqPathInfo = "/" + reqPathInfo;
			}
			if (!reqPathInfo.endsWith("/")) {
				reqPathInfo += "/";
			}
			if (reqPathInfo.startsWith(pathInfo)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void processAjaxRequest(FacesContext context) throws IOException {

		DominoViewJsonService domService = new DominoViewJsonService();

		domService.setDatabaseName(getDatabaseName());
		domService.setViewName(getViewName());
		domService.setDefaultColumns(true);

		domService.setCount(10000);
		domService.setSystemColumns(0);

		IRestService service = domService;

		if (service != null) {

			HttpServletRequest httpRequest = (HttpServletRequest) context.getExternalContext().getRequest();
			HttpServletResponse httpResponse = (HttpServletResponse) context.getExternalContext().getResponse();

			// Disable the XPages response buffer as this will collide with the
			// engine one
			// We mark it as committed and use its delegate instead
			if (httpResponse instanceof XspHttpServletResponse) {
				XspHttpServletResponse r = (XspHttpServletResponse) httpResponse;
				r.setCommitted(true);
				httpResponse = r.getDelegate();
			}

			// Made 2nd param null, I don't think it will affect anything
			RestServiceEngine engine = service.createEngine(context, null, httpRequest, httpResponse);
			engine.processRequest();

			// Save the view state...
			boolean saveState = false;// isState();
			if (saveState) {
				ExtLibUtil.saveViewState(context);
			}
			return;
		}

	}

	@Override
	public void restoreState(FacesContext context, Object state) {

		Object[] values = (Object[]) state;

		super.restoreState(context, values[0]);
		_forId = (String) values[1];
		restUrl = (String) values[2];
		pageName = (String) values[3];
		pathInfo = (String) values[4];
		databaseName = (String) values[5];
		viewName = (String) values[6];
		userNameColumnName = (String) values[7];
		nameColumnName = (String) values[8];
		imageColumnName = (String) values[9];

	}

	@Override
	public Object saveState(FacesContext context) {

		Object[] values = new Object[10];

		values[0] = super.saveState(context);
		values[1] = _forId;
		values[2] = restUrl;
		values[3] = pageName;
		values[4] = pathInfo;
		values[5] = databaseName;
		values[6] = viewName;
		values[7] = userNameColumnName;
		values[8] = nameColumnName;
		values[9] = imageColumnName;

		return values;

	}

}
