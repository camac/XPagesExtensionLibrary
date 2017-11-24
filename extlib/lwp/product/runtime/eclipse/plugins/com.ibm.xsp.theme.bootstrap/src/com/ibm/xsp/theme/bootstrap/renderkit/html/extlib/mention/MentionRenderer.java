package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.mention;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.component.UIScriptCollector;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.theme.bootstrap.components.mention.UIMention;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.TypedUtil;

public class MentionRenderer extends FacesRendererEx {

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {

		if (component instanceof UIMention) {

			UIMention mention = (UIMention) component;

			// Find the Component that we are attaching to (e.g. InputTextArea)
			String forId = mention.getFor();
			UIComponent target = FacesUtil.getComponentFor(component, forId);
			String targetId = target.getClientId(context);

			StringBuilder sb = new StringBuilder();

			String restUrl = mention.getEffectiveRestUrl();

			if (StringUtil.isNotEmpty(restUrl)) {
				encodeRest(context, mention, targetId);
			} else {
				encodeSelectItems(context, mention, targetId);
			}

			sb.append("$(document).ready(function() {");
			sb.append("});");

			// UIScriptCollector.find().addScriptOnce(sb.toString());

			// UIScriptCollector.find().addScriptOnLoad(sb.toString());

		}

	}

	private String jqueryId(String id) {
		return id.replace(":", "\\\\:");
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
	}

	private void encodeSelectItems(FacesContext context, UIMention mention, String targetId) throws IOException {

		List<UIComponent> kids = TypedUtil.getChildren(mention);

		JsonJavaArray data = new JsonJavaArray();

		if (!kids.isEmpty()) {

			Iterator<UIComponent> it = kids.iterator();

			int count = 0;

			while (it.hasNext()) {

				UIComponent kid = it.next();

				if (kid instanceof UISelectItem) {

					JsonJavaObject obj = new JsonJavaObject();

					obj.put("username", ((UISelectItem) kid).getItemValue());
					obj.put("name", ((UISelectItem) kid).getItemLabel());

					data.put(count++, obj);

				}

			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append("$('");
		sb.append(jqueryId(targetId));
		sb.append("').mention({users: ");
		sb.append(data.toString());
		sb.append("})");

		UIScriptCollector.find().addScriptOnLoad(sb.toString());

	}

	private void encodeRest(FacesContext context, UIMention mention, String targetId) throws IOException {

		StringBuilder sb = new StringBuilder();

		String url = mention.getEffectiveRestUrl();

		// should be like './Controls_Jord_Mention.xsp/mention'

		sb.append(" $.getJSON('");
		sb.append(url);
		sb.append("");
		sb.append("', function(result) { ");
		sb.append("x$('");
		sb.append(jqueryId(targetId));
		sb.append("').mention({users:result})");
		sb.append("});");

		UIScriptCollector.find().addScriptOnLoad(sb.toString());

	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

}
