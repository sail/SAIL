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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.ebi.sail.client.ConfigManager;
import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.SecurityManager;
import uk.ac.ebi.sail.client.common.ComplexFilteredRequestItem;
import uk.ac.ebi.sail.client.common.Date2Str;
import uk.ac.ebi.sail.client.common.FilteredRequestItem;
import uk.ac.ebi.sail.client.common.GroupRequestItem;
import uk.ac.ebi.sail.client.common.IntRange;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ParameterPart;
import uk.ac.ebi.sail.client.common.ParameterRequestItem;
import uk.ac.ebi.sail.client.common.Permission;
import uk.ac.ebi.sail.client.common.Range;
import uk.ac.ebi.sail.client.common.ReportRequest;
import uk.ac.ebi.sail.client.common.RequestItem;
import uk.ac.ebi.sail.client.common.Summary;
import uk.ac.ebi.sail.client.common.Variable;
import uk.ac.ebi.sail.client.common.Variant;
import uk.ac.ebi.sail.client.ui.FileDownloader;
import uk.ac.ebi.sail.client.ui.StringUtils;
import uk.ac.ebi.sail.server.util.StringUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.gwtext.client.core.EventCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListener;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.ContainerListenerAdapter;
import com.gwtext.client.widgets.form.Label;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.TableLayout;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.MenuItem;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;

public class ReportTable2 extends Panel {
	private static class ParamRef {
		private String refText;
		private ParameterRequestItem reqItem;

		public ParamRef(String refText, ParameterRequestItem ri) {
			this.refText = refText;
			reqItem = ri;
		}

		public String getRefText() {
			return refText;
		}

		public ParameterRequestItem getReqItem() {
			return reqItem;
		}
	}

	public static final String CFG_ORNT_KEY = "RptOrnt";
	public static final String CFG_SIZE_KEY = "RptSize";

	private ReportRequest request;
	private Summary report;
	private Panel table;
	private boolean defaultHoriz = true;
	private int zoom = 16;
	private List<ParamRef> references = new ArrayList<ParamRef>(10);

	int paramDim;
	int collectionDim;

	public ReportTable2(ReportRequest req, Summary rep) {
		request = req;
		report = rep;

		// Panel intTable = new Panel();
		addClass("reportTable2");
		setLayout(new FitLayout());
		setAutoWidth(true);
		setBorder(false);
		setAutoScroll(true);

		// intTable.setLayout(new TableLayout(1));
		// intTable.setBorder(false);
		// intTable.setAutoScroll(true);

		Label totHdr = new Label("Total records: " + rep.getCount());
		totHdr.setStyleName("totalLbl");
		add(totHdr);

		table = new Panel();
		table.addClass("tablePanel");
		table.setAutoScroll(false);
		// table.setLayout( new TableLayout(len+1) );

		String cgfVal = ConfigManager.getConfigParameter(CFG_ORNT_KEY);

		if (cgfVal == null) {
			ConfigManager.setConfigParameter(CFG_ORNT_KEY,
					Boolean.toString(defaultHoriz));
		} else {
			try {
				defaultHoriz = Boolean.parseBoolean(cgfVal);
			} catch (Exception e) {
			}
		}

		paramDim = 0;

		for (RequestItem rri : req.getRootGroup().getItems()) {
			if (rri instanceof GroupRequestItem)
				paramDim += ((GroupRequestItem) rri).getDimention();
			else
				paramDim++;
		}

		collectionDim = rep.getRelatedCounters().length + 2;
		paramDim += 3;

		Panel resTable = new Panel();

		if (defaultHoriz)
			resTable.setLayout(new TableLayout(paramDim));
		else
			resTable.setLayout(new TableLayout(collectionDim));

		cgfVal = ConfigManager.getConfigParameter(CFG_SIZE_KEY);

		if (cgfVal != null) {
			try {
				zoom = Integer.parseInt(cgfVal);
			} catch (Exception e) {
			}
		}

		table.setStyle("font-size: " + zoom + "pt");

		if (defaultHoriz)
			renderHorizontalTable(rep, req, resTable);
		else
			renderVerticalTable(rep, req, resTable);

		add(table);
		table.add(resTable);

		for (ParamRef ref : references) {
			Panel p = new Panel();
			p.setBorder(false);
			p.setHtml(ref.getRefText());
			add(p);
		}
	}

	public Toolbar getToolBar() {
		Toolbar tb = new Toolbar();

		ToolbarButton tbbv = new ToolbarButton();
		tbbv.setIconCls("arrowDown");
		tbbv.setEnableToggle(true);
		tbbv.setToggleGroup("orientation");
		tbbv.setPressed(!defaultHoriz);
		tbbv.setStateId("V");
		tbbv.addListener(new ButtonListenerAdapter() {
			public void onClick(Button bt, EventObject eo) {
				bt.setPressed(true);
			}
		});
		tb.addButton(tbbv);

		ToolbarButton tbbh = new ToolbarButton();
		tbbh.setIconCls("arrowLeft");
		tbbh.setEnableToggle(true);
		tbbh.setToggleGroup("orientation");
		tbbh.setPressed(defaultHoriz);
		tbbh.setStateId("H");
		tbbh.addListener(new ButtonListenerAdapter() {
			public void onClick(Button bt, EventObject eo) {
				bt.setPressed(true);
			}
		});
		tb.addButton(tbbh);

		tb.addSeparator();

		ToolbarButton tbbzi = new ToolbarButton();
		tbbzi.setIconCls("zoomIn");
		tbbzi.setStateId("ZI");
		tb.addButton(tbbzi);

		ToolbarButton tbbzo = new ToolbarButton();
		tbbzo.setIconCls("zoomOut");
		tbbzo.setStateId("ZO");
		tb.addButton(tbbzo);

		tb.addSeparator();

		ToolbarButton expXLbut = new ToolbarButton();
		expXLbut.setIconCls("exportXL");
		expXLbut.setStateId("EXPXL");
		tb.addButton(expXLbut);

		tb.addSpacer();

		ToolbarButton expCSVbut = new ToolbarButton();
		expCSVbut.setIconCls("exportCSV");
		expCSVbut.setStateId("EXPCSV");
		tb.addButton(expCSVbut);

		ButtonListener obLsnr = new ToolbarListener();

		tbbv.addListener(obLsnr);
		tbbh.addListener(obLsnr);

		tbbzi.addListener(obLsnr);
		tbbzo.addListener(obLsnr);

		expXLbut.addListener(obLsnr);
		expCSVbut.addListener(obLsnr);

		return tb;
	}

	/*
	 * interface CellRendererCallBack { CellRendererInfo getCellRendererInfo(int
	 * ind, RequestItem p, int depth, int level, boolean exprFirst, boolean
	 * groupFirst); }
	 * 
	 * private static class CellRendererInfo { // enum RowType // { // HEADER,
	 * // DATA, // SUMMARY // }
	 * 
	 * private String cls,txt; // private RowType summary; private ReportRequest
	 * reqStr;
	 * 
	 * public CellRendererInfo(String clazz, String text) { cls=clazz; txt=text;
	 * // summary=RowType.DATA; }
	 * 
	 * public CellRendererInfo(String clazz, String text, ReportRequest req) {
	 * cls=clazz; txt=text; reqStr=req; }
	 * 
	 * public String getCellClass() { return cls; }
	 * 
	 * public String getCellText() { return txt; }
	 * 
	 * // public RowType getType() // { // return summary; // }
	 * 
	 * public ReportRequest getRequest() { return reqStr; }
	 * 
	 * }
	 * 
	 * // private void parameterRowDrawer( Panel table, CellRendererCallBack cb
	 * ) // { // grawHGroup(request.getRootGroup(), true, 0, table, 1, cb); // }
	 */

	private int createExpression(int ind, GroupRequestItem grp,
			StringBuilder expr) {
		boolean groupFirst = true;

		boolean needBrace = grp.getItems().size() > 1;
		// boolean topExpr = ind == 1;

		for (RequestItem rri : grp.getItems()) {
			if (rri instanceof ParameterRequestItem) {
				Parameter p = ((ParameterRequestItem) rri).getParameter();

				int d = grp.getItems().size() == grp.getDepth() ? -1 : grp
						.getDepth();

				if (!groupFirst) {
					if (d == 1)
						expr.append(" <span class='orOpText'>OR</span> ");
					else if (d == -1)
						expr.append(" <span class='andOpText'>AND</span> ");
					else
						expr.append("; ");
				} else {
					if (needBrace)
						expr.append("<b>(</b> ");

					if (d != 1 && d != -1)
						expr.append("<span class='anyOpText'>any "
								+ grp.getDepth() + " of</span> ");
				}

				expr.append(p.getCode());
				expr.append("<sup>").append(ind).append("</sup>");

				if (rri instanceof ComplexFilteredRequestItem) {

					String refStr = "<sup>" + ind
							+ "</sup> - parameter with code: '" + p.getCode()
							+ "' and name: '" + p.getName()
							+ "' filtered out by: <ul class='filterDesc'>";

					List<Range> ranges = ((ComplexFilteredRequestItem) rri)
							.getFilter().getRealRanges();

					if (ranges != null) {
						for (Range rg : ranges) {
							Collection<? extends ParameterPart> parts = p
									.getAllVariables();

							if (parts != null) {
								for (ParameterPart pp : parts) {
									if (pp.getId() == rg.getPartID()) {
										refStr += "<li>" + pp.getName()
												+ " within " + rg.toString()
												+ "</li>";
										break;
									}
								}
							}
						}
					}

					List<IntRange> intRanges = ((ComplexFilteredRequestItem) rri)
							.getFilter().getIntRanges();

					if (intRanges != null) {
						for (IntRange rg : intRanges) {
							Collection<? extends ParameterPart> parts = p
									.getAllVariables();

							if (parts != null) {
								for (ParameterPart pp : parts) {
									if (pp.getId() == rg.getPartID()) {
										if (((Variable) pp).getType() == uk.ac.ebi.sail.client.common.Variable.Type.BOOLEAN)
											refStr += "<li>'"
													+ pp.getName()
													+ "' is "
													+ (rg.getLimitLow() == 0 ? "False"
															: "True") + "</li>";
										else if (((Variable) pp).getType() == uk.ac.ebi.sail.client.common.Variable.Type.DATE)
											refStr += "<li>'"
													+ pp.getName()
													+ "' is from "
													+ Date2Str.int2DateStr(rg
															.getLimitLow())
													+ " till "
													+ Date2Str.int2DateStr(rg
															.getLimitHigh());
										else
											refStr += "<li>'" + pp.getName()
													+ "' within "
													+ rg.toString() + "</li>";
										break;
									}
								}
							}
						}
					}

					List<List<Integer>> varis = ((ComplexFilteredRequestItem) rri)
							.getFilter().getVariants();

					if (varis != null) {
						for (List<Integer> ppVari : varis) {
							int partID = ppVari.get(0);

							Collection<? extends ParameterPart> parts = null;
							boolean found = false;

							for (int m = 0; m < 2; m++) {
								if (found)
									break;

								parts = m == 0 ? p.getAllVariables() : p
										.getAllQualifiers();

								if (parts != null) {
									for (ParameterPart pp : parts) {
										if (pp.getId() == partID) {
											found = true;
											refStr += "<li>" + pp.getName()
													+ " is ";

											for (int k = 1; k < ppVari.size(); k++) {
												int vId = ppVari.get(k);
												for (Variant v : pp
														.getVariants()) {
													if (v.getId() == vId) {
														if (k > 1)
															refStr += " OR ";

														refStr += v.getName();

														break;
													}
												}
											}

											refStr += "</li>";
											break;
										}
									}
								}
							}
						}
					}

					refStr += "</ul>";
					references.add(new ParamRef(refStr,
							(ParameterRequestItem) rri));
				} else
					references.add(new ParamRef("<sup>" + ind
							+ "</sup> - parameter with code: '" + p.getCode()
							+ "' and name: '" + p.getName() + "'",
							(ParameterRequestItem) rri));

				ind++;

			} else if (rri instanceof GroupRequestItem) {
				if (needBrace)
					expr.append("<b>(</b> ");

				ind = createExpression(ind, (GroupRequestItem) rri, expr);
			}

			groupFirst = false;
		}

		if (needBrace)
			expr.append(" <b>)</b>");

		return ind;
	}

	/*
	 * private int grawHGroup( GroupRequestItem grp, boolean exprFirst, int ind,
	 * Panel table, int level, CellRendererCallBack cb ) { boolean
	 * groupFirst=true; for( RequestItem rri : grp.getItems() ) { if( rri
	 * instanceof ParameterRequestItem || rri instanceof
	 * ComplexFilteredRequestItem ) { CellRendererInfo cri =
	 * cb.getCellRendererInfo(ind++, rri,
	 * grp.getItems().size()==grp.getDepth()?-1:grp.getDepth(), level,
	 * exprFirst, groupFirst);
	 * 
	 * String cls = cri.getCellClass()+(exprFirst?" boldLeftBorder":"");
	 * 
	 * // int depth = request.getRootGroup().getDepth(); // // if( !exprFirst )
	 * // { // if( depth == -1 || depth ==
	 * request.getRootGroup().getItems().size() ) // cls+=" andBorder"; // else
	 * if( depth == 1 ) // cls+=" orBorder"; // }
	 * 
	 * addCell(table, cri.getCellText(), cls, cri.getRequest());
	 * 
	 * exprFirst=false; } else if( rri instanceof GroupRequestItem )
	 * ind=grawHGroup( (GroupRequestItem) rri, exprFirst, ind, table, level+1,
	 * cb );
	 * 
	 * exprFirst=false; groupFirst=false;
	 * 
	 * }
	 * 
	 * return ind; }
	 */

	private void renderHorizontalTable(Summary rep, final ReportRequest req,
			Panel table) {
		int collectionSumm = 0;

		addCell(table, "Collections", "cellCollectionNameHeader hHeader");
		addCell(table, "Records<br/>in collection",
				"cellCollectionCounterHeader boldLeftBorder hHeader");

		StringBuilder expr = new StringBuilder();

		references.clear();
		createExpression(1, req.getRootGroup(), expr);

		for (int i = 0; i < references.size(); i++) {
			String txt = toHeader(references.get(i).getReqItem().getParameter())
					+ "<sup>" + (i + 1) + "</sup>";
			// String txt =
			// references.get(i).getReqItem().getParameter().getCode()+"<sup>" +
			// (i+1) + "</sup>";

			if (references.get(i).getReqItem() instanceof FilteredRequestItem)
				txt += "<br>(filtered)";

			addCell(table, txt, "cellParameterHeader hHeader"
					+ (i == 0 ? " boldLeftBorder" : ""));
		}

		final int[] summ = new int[references.size() + 1];

		addCell(table, "Result" + "<sup>" + (references.size() + 1) + "</sup>",
				"cellResultHeader  boldLeftBorder hHeader");

		for (Summary br : rep.getRelatedCounters()) {
			if (br.getCount() == 0)
				continue;

			addCell(table, br.getId() > 0 ? DataManager.getInstance()
					.getCollection(br.getId()).getName() : "[All]",
					"cellCollectionName");

			if (br.getId() > 0) {
				ReportRequest rr = new ReportRequest();
				rr.setAllRelations(request.isAllRelations());
				rr.setRelations(request.getRelations());
				rr.setCollectionSplit(true);
				rr.setCollections(new int[] { br.getId() });

				addCell(table, String.valueOf(br.getCount()),
						"cellCollectionCount boldLeftBorder counterCell", rr);
			} else
				addCell(table, String.valueOf(br.getCount()),
						"cellCollectionCount boldLeftBorder counterCell");

			collectionSumm += br.getCount();

			for (int i = 0; i < references.size(); i++) {
				ParamRef pref = references.get(i);

				int count = 0;

				boolean withRels = false;

				if (br.getRelatedCounters() != null) {
					for (Summary pc : br.getRelatedCounters()) {
						if (pc.getId() == pref.getReqItem().getId()) {
							count = pc.getCount();
							withRels = pc.getRelatedCounters() != null
									&& pc.getRelatedCounters().length > 0;
							break;
						}
					}
				}

				summ[i] += count;

				ReportRequest rr = new ReportRequest();
				rr.setAllRelations(request.isAllRelations());
				rr.setRelations(request.getRelations());

				// rr.setAndOperation( request.isAndOperation() );

				if (br.getId() > 0) {
					rr.setCollectionSplit(true);
					rr.setCollections(new int[] { br.getId() });
				}

				rr.add(pref.getReqItem());

				String percent = br.getCount() == 0 ? ""
						: "<span class='percentage'>("
								+ String.valueOf(count * 100 / br.getCount())
								+ "%)</span>";

				addCell(table, String.valueOf(count)
						+ (withRels ? "<sup>+R</sup>" : "") + percent,
						"cellParameterCount counterCell"
								+ (i == 0 ? " boldLeftBorder" : ""), rr);
			}

			ReportRequest rr = new ReportRequest();
			rr.setAllRelations(request.isAllRelations());
			rr.setRelations(request.getRelations());

			// rr.setAndOperation( request.isAndOperation() );

			if (br.getId() > 0) {
				rr.setCollectionSplit(true);
				rr.setCollections(new int[] { br.getId() });
			}

			for (RequestItem rri : request.getRootGroup().getItems())
				rr.add(rri);

			int resCnt = br.getRelatedCounters() != null
					&& br.getRelatedCounters().length > 0 ? br
					.getRelatedCounters()[0].getCount() : 0;

			String resStr = br.getCount() == 0 ? "0" : String.valueOf(resCnt)
					+ "<span class='percentage'>("
					+ String.valueOf(resCnt * 100 / br.getCount())
					+ "%)</span>";

			addCell(table, resStr,
					"cellResultCount boldLeftBorder counterCell", rr);

			summ[summ.length - 1] += resCnt;
		}

		addCell(table, "Summary", "cellSummaryHeader hSummary");

		if (request.isCollectionSplit() && request.getCollections() != null) {
			ReportRequest rr = new ReportRequest();
			rr.setAllRelations(request.isAllRelations());
			rr.setRelations(request.getRelations());
			rr.setCollectionSplit(true);
			rr.setCollections(request.getCollections());

			addCell(table,
					String.valueOf(collectionSumm),
					"cellCollectionSummary boldLeftBorder hSummary counterCell",
					rr);
		} else
			addCell(table, String.valueOf(collectionSumm),
					"cellCollectionSummary boldLeftBorder hSummary counterCell");

		for (int i = 0; i < references.size(); i++) {
			ParamRef pref = references.get(i);

			ReportRequest rr = new ReportRequest();
			rr.setAllRelations(request.isAllRelations());
			rr.setRelations(request.getRelations());

			rr.setCollectionSplit(request.isCollectionSplit());
			rr.setCollections(request.getCollections());

			rr.add(pref.getReqItem());

			addCell(table, String.valueOf(summ[i]),
					"cellParameterSummary hSummary counterCell"
							+ (i == 0 ? " boldLeftBorder" : ""), rr);
		}

		addCell(table, String.valueOf(summ[summ.length - 1]),
				"cellResultSummary boldLeftBorder hSummary counterCell",
				request);

		references.add(new ParamRef("<sup>" + (references.size() + 1)
				+ "</sup> - " + expr, null));
	}

	/*
	 * private int drawVGroup(GroupRequestItem grp, Summary[] sLst,
	 * StringBuilder expr, boolean exprFirst, int ind, Panel table) { boolean
	 * groupFirst = true;
	 * 
	 * int op = grp.getDepth();
	 * 
	 * if( op == grp.getItems().size() ) op=-1;
	 * 
	 * for( RequestItem ri : grp.getItems() ) { if( ri instanceof
	 * GroupRequestItem ) ind = drawVGroup( (GroupRequestItem) ri, sLst, expr,
	 * exprFirst, ind, table); else { Parameter p;
	 * 
	 * if(ri instanceof ParameterRequestItem) p = ((ParameterRequestItem)
	 * ri).getParameter(); else if(ri instanceof ComplexFilteredRequestItem) p =
	 * ((ComplexFilteredRequestItem) ri).getFilter().getParameter(); else
	 * continue;
	 * 
	 * 
	 * String cls = exprFirst ? " boldTopBorder" : ""; // +
	 * (request.isAndOperation() ? " andTopBorder" : " orTopBorder");
	 * 
	 * references.add( new ParamRef("<sup>" + ind +
	 * "</sup> - parameter with code: '" + p.getCode() + "' and name: '" +
	 * p.getName()+ "'",(ParameterRequestItem)ri) );
	 * 
	 * addCell(table, p.getCode() + "<sup>" + (ind++) + "</sup>",
	 * "cellParameterHeader vHeader" + cls);
	 * 
	 * int sum=0; for(Summary br : sLst) { int count = 0;
	 * 
	 * if( br.getRelatedCounters() != null ) { for(Summary pc :
	 * br.getRelatedCounters()) { if(pc.getId() == ri.getId()) { count =
	 * pc.getCount(); break; } } }
	 * 
	 * ReportRequest rr = new ReportRequest(); rr.setAllRelations(
	 * request.isAllRelations() ); rr.setRelations( request.getRelations() );
	 * 
	 * if( br.getId() > 0 ) { rr.setCollectionSplit(true); rr.setCollections(new
	 * int[]{br.getId()}); }
	 * 
	 * rr.add(ri);
	 * 
	 * String resStr =
	 * br.getCount()==0?"0":String.valueOf(count)+"<span class='percentage'>("
	 * +String.valueOf(count*100/br.getCount())+"%)</span>";
	 * 
	 * addCell(table, resStr, "cellParameterCount counterCell" + cls, rr);
	 * sum+=count; }
	 * 
	 * ReportRequest rr = new ReportRequest(); rr.setAllRelations(
	 * request.isAllRelations() ); rr.setRelations( request.getRelations() );
	 * 
	 * rr.setCollectionSplit(true); rr.setCollections(request.getCollections());
	 * 
	 * rr.add(ri);
	 * 
	 * addCell(table,String.valueOf(sum),"cellParameterSummary counterCell vSummary"
	 * +cls,rr);
	 * 
	 * 
	 * if( groupFirst ) { expr.append(" ( ");
	 * 
	 * if( op != 1 && op != -1 ) expr.append(op).append(" of ");
	 * 
	 * } else { if( op == 1) expr.append(" <span class='orOpText'>OR</span> ");
	 * else if( op == -1 ) expr.append(" <span class='andOpText'>AND</span> ");
	 * else expr.append("; "); }
	 * 
	 * expr.append( p.getCode() ); }
	 * 
	 * exprFirst = false; groupFirst= false; }
	 * 
	 * expr.append(" ) ");
	 * 
	 * return ind; }
	 */

	// JM
	protected String toHeader(Parameter p) {
		String r = p.getName();
		return StringUtils.wrapText(r, 10, "<br/>");
	}

	private void renderVerticalTable(Summary rep, ReportRequest req, Panel table) {
		StringBuilder expr = new StringBuilder();

		references.clear();
		createExpression(1, req.getRootGroup(), expr);

		addCell(table, "Collections", "cellCollectionNameHeader vHeader");

		for (Summary br : rep.getRelatedCounters())
			addCell(table, br.getId() > 0 ? DataManager.getInstance()
					.getCollection(br.getId()).getName() : "[All]",
					"cellCollectionName");

		addCell(table, "Summary", "cellSummaryHeader vSummary");
		addCell(table, "Records<br/>in collection",
				"cellCollectionCounterHeader  boldTopBorder vHeader");

		int sum = 0;
		for (Summary br : rep.getRelatedCounters()) {
			ReportRequest rr = new ReportRequest();
			rr.setAllRelations(request.isAllRelations());
			rr.setRelations(request.getRelations());
			rr.setCollectionSplit(true);
			rr.setCollections(new int[] { br.getId() });

			addCell(table, String.valueOf(br.getCount()),
					"cellCollectionCount  boldTopBorder counterCell", rr);

			sum += br.getCount();
		}

		{ // This block is just to hide rri
			// RequestItem rri = request.getRootGroup().getItems().get(0);
			if (request.isCollectionSplit() && request.getCollections() != null) {
				ReportRequest rr = new ReportRequest();
				rr.setAllRelations(request.isAllRelations());
				rr.setRelations(request.getRelations());
				rr.setCollectionSplit(true);
				rr.setCollections(request.getCollections());

				addCell(table,
						String.valueOf(sum),
						"cellCollectionSummary boldTopBorder vSummary counterCell",
						rr);
			} else
				addCell(table, String.valueOf(sum),
						"cellCollectionSummary boldTopBorder vSummary counterCell");
		}

		for (int i = 0; i < references.size(); i++) {
			ParamRef pr = references.get(i);

			String cls = i == 0 ? " boldTopBorder" : "";

			addCell(table, toHeader(pr.getReqItem().getParameter()) + "<sup>"
					+ (i + 1) + "</sup>", "cellParameterHeader vHeader" + cls);

			// addCell(table, pr.getReqItem().getParameter().getCode() + "<sup>"
			// + (i+1) + "</sup>", "cellParameterHeader vHeader" + cls );

			sum = 0;
			for (Summary br : rep.getRelatedCounters()) {
				int count = 0;

				if (br.getRelatedCounters() != null) {
					for (Summary pc : br.getRelatedCounters()) {
						if (pc.getId() == pr.getReqItem().getId()) {
							count = pc.getCount();
							break;
						}
					}
				}

				ReportRequest rr = new ReportRequest();
				rr.setAllRelations(request.isAllRelations());
				rr.setRelations(request.getRelations());

				if (br.getId() > 0) {
					rr.setCollectionSplit(true);
					rr.setCollections(new int[] { br.getId() });
				}

				rr.add(pr.getReqItem());

				String resStr = br.getCount() == 0 ? "0" : String
						.valueOf(count)
						+ "<span class='percentage'>("
						+ String.valueOf(count * 100 / br.getCount())
						+ "%)</span>";

				addCell(table, resStr, "cellParameterCount counterCell" + cls,
						rr);
				sum += count;
			}

			ReportRequest rr = new ReportRequest();
			rr.setAllRelations(request.isAllRelations());
			rr.setRelations(request.getRelations());

			rr.setCollectionSplit(true);
			rr.setCollections(request.getCollections());

			rr.add(pr.getReqItem());

			addCell(table, String.valueOf(sum),
					"cellParameterSummary counterCell vSummary" + cls, rr);

		}

		// StringBuilder expr = new StringBuilder();
		//
		// int ind = drawVGroup(request.getRootGroup(),
		// rep.getRelatedCounters(), expr, true, 1, table);
		//
		// references.add( new ParamRef("<sup>" + ind + "</sup> - "+expr, null
		// ));

		addCell(table, "Result<sup>" + (references.size() + 1) + "</sup>",
				"cellResultHeader boldTopBorder vHeader");
		sum = 0;
		for (Summary br : rep.getRelatedCounters()) {
			int count = br.getRelatedCounters() != null
					&& br.getRelatedCounters().length > 0 ? br
					.getRelatedCounters()[0].getCount() : 0;

			ReportRequest rr = new ReportRequest();
			rr.setAllRelations(request.isAllRelations());
			rr.setRelations(request.getRelations());

			if (br.getId() > 0) {
				rr.setCollectionSplit(true);
				rr.setCollections(new int[] { br.getId() });
			}

			for (RequestItem rri : request.getRootGroup().getItems())
				rr.add(rri);

			String resStr = br.getCount() == 0 ? "0" : String.valueOf(count)
					+ "<span class='percentage'>("
					+ String.valueOf(count * 100 / br.getCount()) + "%)</span>";

			addCell(table, resStr, "cellResultCount boldTopBorder counterCell",
					rr);

			sum += count;
		}

		addCell(table, String.valueOf(sum),
				"cellResultSummary boldTopBorder counterCell vSummary", request);

		references.add(new ParamRef("<sup>" + (references.size() + 1)
				+ "</sup> - " + expr, null));

	}

	private void addCell(Panel tbl, String text, String cls) {
		Panel lp = new Panel();
		AdvTableLayoutData td = new AdvTableLayoutData();

		lp.setAutoHeight(true);
		lp.setBorder(false);
		td.setCellClass(cls);
		lp.setCls("cellPanel");
		lp.setHtml(text);
		tbl.add(lp, td);
	}

	private void addCell(Panel tbl, String text, String cls,
			final ReportRequest req) {
		Panel lp = new Panel();
		AdvTableLayoutData td = new AdvTableLayoutData();

		lp.setAutoHeight(true);
		lp.setBorder(false);
		td.setCellClass(cls);
		lp.setHtml(text);
		lp.setCls("cellPanel");
		tbl.add(lp, td);

		lp.addListener(new ContainerListenerAdapter() {
			public void onRender(Component component) {
				CellClickListener cl = new CellClickListener(req);
				component.getEl().addListener("contextmenu", cl);
				component.getEl().addListener("click", cl);
			}
		});
	}

	class ToolbarListener extends ButtonListenerAdapter {

		public ToolbarListener() {
		}

		public void onClick(Button button, EventObject e) {
			if ("V".equals(button.getStateId())) {
				table.removeAll(true);

				Panel resTable = new Panel();
				resTable.setStyle("font-size: " + zoom + "pt");
				resTable.addClass("tablePanel");

				resTable.setLayout(new TableLayout(collectionDim));

				renderVerticalTable(report, request, resTable);

				table.add(resTable);
				doLayout();

				defaultHoriz = false;
				ConfigManager.setConfigParameter(CFG_ORNT_KEY,
						Boolean.toString(defaultHoriz));
			} else if ("H".equals(button.getStateId())) {
				table.removeAll(true);

				Panel resTable = new Panel();
				resTable.setStyle("font-size: " + zoom + "pt");
				resTable.addClass("tablePanel");

				table.add(resTable);

				resTable.setLayout(new TableLayout(paramDim));

				renderHorizontalTable(report, request, resTable);

				doLayout();

				defaultHoriz = true;
				ConfigManager.setConfigParameter(CFG_ORNT_KEY,
						Boolean.toString(defaultHoriz));

			} else if ("ZO".equals(button.getStateId())) {
				if (zoom <= 4)
					return;

				zoom -= 2;
				table.getComponents()[0].setStyle("font-size: " + zoom + "pt");

				ConfigManager.setConfigParameter(CFG_SIZE_KEY,
						Integer.toString(zoom));
			} else if ("ZI".equals(button.getStateId())) {
				if (zoom >= 60)
					return;

				zoom += 2;
				table.getComponents()[0].setStyle("font-size: " + zoom + "pt");

				ConfigManager.setConfigParameter(CFG_SIZE_KEY,
						Integer.toString(zoom));
			} else if ("EXPXL".equals(button.getStateId())) {
				String reqStr = request.toSerialString();

				FileDownloader.downloadFile(GWT.getModuleBaseURL()
						+ "reportExport?request=" + reqStr + "&time="
						+ System.currentTimeMillis());
			} else if ("EXPCSV".equals(button.getStateId())) {
				String reqStr = request.toSerialString();

				FileDownloader.downloadFile(GWT.getModuleBaseURL()
						+ "reportExport?format=csv&request=" + reqStr
						+ "&time=" + System.currentTimeMillis());
			}
		}

	}

	static class CellClickListener extends BaseItemListenerAdapter implements
			EventCallback {

		private ReportRequest req;

		public CellClickListener(ReportRequest rc) {
			req = rc;
		}

		public void execute(EventObject e) {
			e.stopEvent();

			Menu clickMenu = new Menu();

			MenuItem itm = new MenuItem();
			itm.setText("Download IDs");
			itm.addListener(this);
			itm.setStateId("ids");
			clickMenu.addItem(itm);

			itm = new MenuItem();
			itm.setText("Download Records");
			itm.setStateId("records");
			itm.addListener(this);

			clickMenu.addItem(itm);

			itm = new MenuItem();
			itm.setText("Show request");
			itm.setStateId("req");
			itm.addListener(this);

			clickMenu.addItem(itm);

			clickMenu.showAt(e.getXY());
		}

		@Override
		public void onClick(BaseItem item, EventObject e) {
			if (item.getStateId().equals("ids")) {
				if (SecurityManager.getAccessController().checkPermission(
						Permission.GET_RECORD_ID)) {
					Window.open(
							GWT.getModuleBaseURL()
									+ "dataExport?idsOnly=true&request="
									+ req.toSerialString(), "_blank", "");
					// MessageBox.confirm("Confirm IDs download",
					// "Do you want to download " + rc.getCount() +
					// " IDs for this cell?",
					// new MessageBox.ConfirmCallback()
					// {
					//
					// public void execute(String btnID)
					// {
					// if(!"yes".equals(btnID))
					// return;
					//
					// IDReporter.getInstance().report(rc.getRequest());
					//
					// }
					// });
				} else
					MessageBox.alert("Permission denied",
							"You have no permission to download record IDs");
			} else if (item.getStateId().equals("records")) {
				if (SecurityManager.getAccessController().checkPermission(
						Permission.GET_RECORD)) {
					Window.open(GWT.getModuleBaseURL() + "dataExport?request="
							+ req.toSerialString(), "_blank", "");
				} else
					MessageBox.alert("Permission denied",
							"You have no permission to download records");
			} else
				MessageBox.alert("Cell request", req.toSerialString());

		}
	}

}
