/**
 * SAIL - biological samples availability index
 * 
 * Copyright (C) 2008,2009 Microarray Informatics Team, EMBL-European Bioinformatics Institute
 *
 *   This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *
 *  @author Mikhail Gostev <gostev@ebi.ac.uk>
 *
 */

package uk.ac.ebi.sail.client.ui.module;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.sail.client.BackendService;
import uk.ac.ebi.sail.client.BackendServiceAsync;
import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.InitListener;
import uk.ac.ebi.sail.client.LinkClickListener;
import uk.ac.ebi.sail.client.LinkManager;
import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ReportRequest;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.common.Study;
import uk.ac.ebi.sail.client.common.Summary;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.widget.ErrorBox;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Viewport;
import com.gwtext.client.widgets.layout.TableLayout;

public class UserPanel implements InitListener, LinkClickListener {
	// static ReportParameterTable reportPrmTable;
	static TabPanel appPanel;
	static int reportCounter = 1;
	ReportConstructorPanel constructorTab;
	BasicConstructorPanel basicConstructorTab;

	public void doInit() {
		MessageBox.wait("Initializing...", "Initializing interface");

		LinkManager.getInstance().addLinkClickListener("welcome", this);

		appPanel = new TabPanel();
		// appPanel.setBodyBorder(false);
		// appPanel.setResizeTabs(true);
		// appPanel.setTabWidth(160);
		appPanel.setEnableTabScroll(true);
		// appPanel.setAutoScroll(true);
		appPanel.setActiveTab(0);
		appPanel.setLayoutOnTabChange(true);

		Panel wp = new WelcomePanel();
		wp.setClosable(true);
		wp.setIconCls("rootNodeIcon");
		appPanel.add(wp);

		CollectionViewPanel rp = new CollectionViewPanel(DataManager
				.getInstance().getSampleCollectionManager());
		rp.setTitle("Summary");
		// rp.setId("Summary");
		rp.setClosable(false);
		rp.setIconCls("summaryIcon");
		appPanel.add(rp);

		constructorTab = new ReportConstructorPanel(new Action[] { new Action(
				"Extra", "extra", "extraIcon", new Action[] {
						new Action(null, null),
						new Action("View parameter", "up_view", "viewIcon",
								null) }) });
		// , new
		// Action("Export","export"+ActionFlags.separator+ActionFlags.EMPTY)});

		constructorTab.addReportListener(new ReportAction());
		constructorTab.setListener(new ParameterAction());
		constructorTab.setStudyInfoListener(new StudyInfoAction());
		constructorTab.setCollectionInfoListener(new CollectionInfoAction());
		constructorTab.setIconCls("constructIcon");

		appPanel.add(constructorTab);

		basicConstructorTab = new BasicConstructorPanel(
				new Action[] { new Action("Extra", "extra", "extraIcon",
						new Action[] {
								new Action(null, null),
								new Action("View parameter", "up_view",
										"viewIcon", null) }) });

		basicConstructorTab.addReportListener(new ReportAction());
		basicConstructorTab.setListener(new ParameterAction());
		// basicConstructorTab.setStudyInfoListener( new StudyInfoAction() );
		// basicConstructorTab.setCollectionInfoListener( new
		// CollectionInfoAction() );
		basicConstructorTab.setIconCls("constructIcon");
		// constructorTab.setId("constructor");

		appPanel.add(basicConstructorTab);

		DataUploadPanel dup = new DataUploadPanel();
		dup.setTitle("Data Upload");

		appPanel.add(dup);

		appPanel.doLayout();

		new Viewport(appPanel);

		MessageBox.hide();

	}

	static class ReportAction implements MakeReportListener {

		public void makeReport(final ReportRequest req) {

			BackendServiceAsync async = BackendService.Util.getInstance();
			async.getReport2(req, new AsyncCallback<Summary>() {

				public void onFailure(Throwable arg0) {
					arg0.printStackTrace();
					MessageBox.alert("Error!", "Error: " + arg0.getMessage(),
							null);
				}

				public void onSuccess(Summary res) {
					String repId = "Report " + (reportCounter++);

					Panel reportItem = new Panel();
					reportItem.setClosable(true);
					reportItem.setTitle(repId);
					reportItem.setCls("report-tab");
					reportItem.setIconCls("reportIcon");
					reportItem.setId(repId);
					reportItem.setAutoScroll(true);
					reportItem.setLayout(new TableLayout(1));
					// reportItem.setLayout(new FitLayout());

					// item.setScrollMode(Scroll.AUTO);

					ReportTable2 rt = new ReportTable2(req, res);
					reportItem.setTopToolbar(rt.getToolBar());

					reportItem.add(rt);

					appPanel.add(reportItem);

					appPanel.setActiveTab(repId);

				}
			});

		}

	}

	class ParameterAction implements ObjectAction<Parameter> {
		private Map<String, ObjectAction<Parameter>> actionMap;

		ParameterAction() {
			actionMap = new TreeMap<String, ObjectAction<Parameter>>();
			actionMap.put("export", new ExportParameterListener());
			actionMap.put("up_view", new ViewParameterAction());
		}

		public void doAction(String actName, Parameter p) {
			ObjectAction<Parameter> act = actionMap.get(actName);

			if (act != null)
				act.doAction(actName, p);
		}

		public void doMultyAction(String actName, List<Parameter> lp) {
			ObjectAction<Parameter> act = actionMap.get(actName);

			if (act != null)
				act.doMultyAction(actName, lp);
		}
	}

	class ExportParameterListener implements ObjectAction<Parameter> {

		public void doAction(String actName, Parameter p) {
			// TODO Auto-generated method stub

		}

		public void doMultyAction(String actName, List<Parameter> lp) {
			// TODO Auto-generated method stub

		}

	}

	class ViewParameterAction implements ObjectAction<Parameter> {

		public void doAction(String actName, Parameter p) {
			final Panel parameterEditTab = new Panel();
			parameterEditTab.setAutoScroll(true);
			parameterEditTab.setTitle("View parameter");
			parameterEditTab.setIconCls("tab-icon");
			parameterEditTab.setClosable(true);
			parameterEditTab.setAutoWidth(true);

			ParameterEditPanel prmEditTab = new ParameterEditPanel(false);
			prmEditTab.setTitle("View parameter");

			prmEditTab.setObjectActionListener(new ObjectAction<Parameter>() {

				public void doAction(String done, Parameter p) {
					System.out.println("Parameter action: " + done);
					appPanel.remove(parameterEditTab.getId());

				}

				public void doMultyAction(String actName, List<Parameter> lp) {
				}
			});

			parameterEditTab.add(prmEditTab);
			parameterEditTab.setPaddings(15);

			prmEditTab.setParameter(new Parameter(p));

			appPanel.add(parameterEditTab);
			appPanel.setActiveTab(parameterEditTab.getId());

		}

		public void doMultyAction(String actName, List<Parameter> lp) {
			// TODO Auto-generated method stub

		}
	}

	class CollectionEditAction implements ObjectAction<SampleCollection> {

		public void doAction(final String actName, SampleCollection p) {
			ErrorBox.showError("This option only available in administration mode");
			return;

		}

		public void doMultyAction(String actName, List<SampleCollection> lp) {
			int n = 0;
			for (SampleCollection p : lp) {
				System.out.println("Selected[" + (n++) + "]: " + p.getName()
						+ " Action: " + actName);
			}

		}
	}

	class CollectionInfoAction implements ObjectAction<SampleCollection>,
			LinkClickListener {
		CollectionInfoAction() {
			LinkManager.getInstance().addLinkClickListener("collectionInfo",
					this);
		}

		@Override
		public void doAction(String actName, SampleCollection kh) {
			final Panel collectionEditTab = new Panel();
			collectionEditTab.setAutoScroll(true);
			collectionEditTab.setTitle("Collection information");
			collectionEditTab.setIconCls("tab-icon");
			collectionEditTab.setClosable(true);
			collectionEditTab.setAutoWidth(true);

			collectionEditTab.add(new CollectionInfoPanel2(kh));

			appPanel.add(collectionEditTab);
			appPanel.setActiveTab(collectionEditTab.getId());
		}

		@Override
		public void doMultyAction(String actName, List<SampleCollection> lp) {
		}

		@Override
		public void linkClicked(String param) {
			int khId = Integer.parseInt(param);

			doAction(null, DataManager.getInstance().getCollection(khId));

		}

	}

	class StudyInfoAction implements ObjectAction<Study> {

		@Override
		public void doAction(String actName, Study st) {
			final Panel studyEditTab = new Panel();
			studyEditTab.setAutoScroll(true);
			studyEditTab.setTitle("Study information");
			studyEditTab.setIconCls("tab-icon");
			studyEditTab.setClosable(true);
			studyEditTab.setAutoWidth(true);
			studyEditTab.setAutoScroll(true);

			studyEditTab.add(new StudyInfoPanel2(st));

			appPanel.add(studyEditTab);
			appPanel.setActiveTab(studyEditTab.getId());
		}

		@Override
		public void doMultyAction(String actName, List<Study> lp) {
		}

	}

	@Override
	public void linkClicked(String param) {
		if (param.equals(WelcomePanel.summaryLink))
			appPanel.setActiveTab(1);
		else if (param.equals(WelcomePanel.constructorLink))
			appPanel.setActiveTab(2);

	}

}
