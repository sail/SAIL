package uk.ac.ebi.sail.client.ui.module;

import java.util.List;
import java.util.Map;

import uk.ac.ebi.sail.client.ConfigManager;
import uk.ac.ebi.sail.client.common.Report;
import uk.ac.ebi.sail.client.common.ReportRequest;
import uk.ac.ebi.sail.client.common.SimpleCounter;
import uk.ac.ebi.sail.client.ui.CellClickListener;
import uk.ac.ebi.sail.client.ui.ReportCell;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListener;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.ContainerListenerAdapter;
import com.gwtext.client.widgets.form.Label;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.TableLayout;


public class ReportTable extends Panel
{
 public static final String CFG_ORNT_KEY="RptOrnt";
 public static final String CFG_SIZE_KEY="RptSize";
 
 private SimpleCounter   chain;
 private ReportRequest   request;
 private Panel           table;
 private List<String>    header;
 private boolean defaultHoriz=true;
 private int zoom=16;
 
 public ReportTable( ReportRequest req, Report rep )
 {
  request=req;
  
//  Panel intTable = new Panel();
  addClass("reportTable");
  setLayout( new FitLayout() );
  setBorder(false);
  setAutoScroll(true);  
  
//  intTable.setLayout(new TableLayout(1));
//  intTable.setBorder(false);
//  intTable.setAutoScroll(true);
  
  chain = rep;
  header = rep.getHeaders();

  Label totHdr = new Label("Total records: " + chain.getCount());
  totHdr.setStyleName("totalLbl");
  add( totHdr );

  countSplit(chain);

  table = new Panel();
  table.addClass("tablePanel");
  table.setAutoScroll(false);
//  table.setLayout( new TableLayout(len+1) );

  String cgfVal = ConfigManager.getConfigParameter(CFG_ORNT_KEY);
  
  if( cgfVal == null )
  {
   ConfigManager.setConfigParameter(CFG_ORNT_KEY, Boolean.toString(defaultHoriz) );
  }
  else
  {
   try
   {
    defaultHoriz=Boolean.parseBoolean(cgfVal);
   }
   catch (Exception e) 
   {}
  }
  
  if( defaultHoriz )
   table.setLayout( new TableLayout(chain.getSplit()+1) );
  else
   table.setLayout( new TableLayout(header.size()) );

  cgfVal = ConfigManager.getConfigParameter(CFG_SIZE_KEY);

  if( cgfVal != null )
  {
   try
   {
    zoom=Integer.parseInt(cgfVal);
   }
   catch (Exception e) 
   {}
  }
  
  
  table.setStyle("font-size: "+zoom+"pt");

  ReportCell[][] dtbl = new ReportCell[header.size()][];

  int i = 0;
  for(String o : header)
  {
   dtbl[i] = new ReportCell[chain.getSplit() + 1];
   
   final ReportCell rc = new ReportCell(0);
   rc.setHTML(o);
   
   dtbl[i][0]=rc;

   i++;
  }
  drawResult(chain, 0, dtbl);
  
  if( defaultHoriz )
   renderHorizontalTable(dtbl, table);
  else
   renderVerticalTable(dtbl, table);
  

  add(table);
 }
 
 public Toolbar getToolBar()
 {
  Toolbar tb = new Toolbar();
  
  ToolbarButton tbbv = new ToolbarButton();
  tbbv.setIconCls("arrowDown");
  tbbv.setEnableToggle(true);
  tbbv.setToggleGroup("orientation");
  tbbv.setPressed(!defaultHoriz);
  tbbv.setDisabled(!defaultHoriz);
  tbbv.setStateId("V");
  tb.addButton( tbbv );
  
  ToolbarButton tbbh = new ToolbarButton();
  tbbh.setIconCls("arrowLeft");
  tbbh.setEnableToggle(true);
  tbbh.setToggleGroup("orientation");  
  tbbh.setPressed(defaultHoriz);
  tbbh.setDisabled(defaultHoriz);
  tbbh.setStateId("H");
  tb.addButton( tbbh );
 
  tb.addSeparator();
  
  ToolbarButton tbbzi = new ToolbarButton();
  tbbzi.setIconCls("zoomIn");
  tbbzi.setStateId("ZI");
  tb.addButton( tbbzi );

  ToolbarButton tbbzo = new ToolbarButton();
  tbbzo.setIconCls("zoomOut");
  tbbzo.setStateId("ZO");
  tb.addButton( tbbzo );
  
  ButtonListener obLsnr = new OrientationListener(tbbv,tbbh) ;
  
  tbbv.addListener( obLsnr );
  tbbh.addListener( obLsnr );

  tbbzi.addListener( obLsnr );
  tbbzo.addListener( obLsnr );
 
  return tb;
 }

 private void renderHorizontalTable( ReportCell[][] dtbl, Panel table )
 {
  for( int i=0; i < dtbl.length; i++  )
  {
   for(int j = 0; j < dtbl[i].length; j++)
   {
    boolean ishdr = true;

    if(j > 0)
     ishdr = false;

    int span = 1;
    
    final ReportCell rc = dtbl[i][j];
    while(j < dtbl[i].length - 1 && dtbl[i][j + 1] == rc)
    {
     span++;
     j++;
    }

    AdvTableLayoutData td = new AdvTableLayoutData();
    td.setColspan(span);

    if(ishdr)
     td.setCellClass("cellhdr");
    else
     td.setCellClass("cellval");

    Panel lp = new Panel();

    if(!ishdr)
    {
     lp.addListener(new ContainerListenerAdapter()
     {
      
      public void onRender(Component component)
      {
       component.getEl().addListener("click", new CellClickListener(rc) );
      }
     });
    }
    // lp.setTitle(" ");
    // lp.setTools(new Tool[]{new Tool(Tool.SAVE,(Function)null,"Save")});
    lp.setAutoHeight(true);
    lp.setBorder(false);
    lp.setHtml(rc.getHTML());
    // Label lf = new Label(s);
    table.add(lp, td);

    if(!ishdr)
     lp.setStyleName("lblval");
    else
     lp.setStyleName("lblhdr");

    // tbl.add(new LabelField(dtbl[i][j]));
   }
  }

//  doLayout();
//  table.doLayout();
 }
 
 private void renderVerticalTable( ReportCell[][] dtbl, Panel table )
 {
  for( int i=0; i < dtbl[0].length; i++  )
  {
   for(int j = 0; j < dtbl.length; j++)
   {
    boolean ishdr = i == 0;

    int span = 1;
    
    final ReportCell rc = dtbl[j][i];
    
    if( rc == null )
     continue;
    
    int k=i+1;
    while(k < dtbl[j].length && dtbl[j][k] == rc)
    {
     dtbl[j][k]=null;

     span++;
     k++;
     
    }

    AdvTableLayoutData td = new AdvTableLayoutData();
    
    if( span > 1 )
     td.setRowspan(span);

    if(ishdr)
     td.setCellClass("cellhdr");
    else
     td.setCellClass("cellval");

    Panel lp = new Panel();

    if(!ishdr)
    {
     lp.addListener(new ContainerListenerAdapter()
     {
      
      public void onRender(Component component)
      {
       component.getEl().addListener("click", new CellClickListener(rc) );
      }
     });
    }

    lp.setAutoHeight(true);
    lp.setBorder(false);
    lp.setHtml(rc.getHTML());
    table.add(lp, td);

    if(!ishdr)
     lp.setStyleName("lblval");
    else
     lp.setStyleName("lblhdr");

   }
  }

 }

 
 
 
 private void drawResult(SimpleCounter c, int level, ReportCell[][] dtbl)
 {
  if( level >= dtbl.length )
   return;
  
  if(c.getRef() == null && c.getRefMap() == null)
  {
   ReportCell rc = new ReportCell(0);
   
   for( int k=level; k < dtbl.length; k++)
   {
    int pos=1;
    for(; pos < dtbl[k].length; pos++)
    {
     if( dtbl[k][pos] == null )
     {
      dtbl[k][pos]=rc;
      break;
     }
    }


   }
   return;
  }

//  if(level != clevel)
//  {
//   String title = null;
//   Object o = header.get(level);
//
//   if(o instanceof Parameter)
//    tbl.add(new LabelField(((Parameter) o).getName()));
//   else
//   {
//    Qualifier q = (Qualifier) o;
//    title = q.getParameter().getName() + "<br>" + q.getName();
//    tbl.add(new LabelField(title));
//    // tbl.add(new LabelField("N"));
//   }
//
//   clevel = level;
//  }

  int pos=1;
  for(; pos < dtbl[level].length; pos++)
  {
   if( dtbl[level][pos] == null )
    break;
  }
   
  if(c.getRef().getRefMap() == null)
  {
   if(c.getRef().getSplit() == 1)
    dtbl[level][pos]=new ReportCell(c.getRef(), null, request.getRootGroup().getItems().get(level),
      level!=0?dtbl[level-1][pos].getRequest():null);

     //dtbl[level][pos]=String.valueOf(c.getRef().getCount())+"<br>"+c.getRef().getParameterID()+":"+c.getRef().getPartID()+":"+c.getRef().getVariantID();
//    tbl.add(new LabelField(String.valueOf(c.getRef().getCount())));
   else
   {
    ReportCell rc =  new ReportCell( c.getRef(), null, request.getReportRequestItems().get(level),
      level!=0?dtbl[level-1][pos].getRequest():null );
    
    for( int i=0; i < c.getRef().getSplit(); i++ )
     dtbl[level][pos++]=rc;
//    TableData td = new TableData();
//    td.setColspan(c.getRef().getSplit());
//    tbl.add(new LabelField(String.valueOf(c.getRef().getCount())), td);
   }

   drawResult(c.getRef(), level + 1,dtbl);
  }
  else
  {
   SimpleCounter anysc=null;
   for(Map.Entry<String, SimpleCounter> me : c.getRef().getRefMap().entrySet())
   {
    if( me.getValue().getParameterID() == SimpleCounter.ANY )
    {
     anysc=me.getValue();
     continue;
    }

    ReportCell rc = new ReportCell(me.getValue(), me.getKey(), request.getReportRequestItems().get(level),
      level!=0?dtbl[level-1][pos].getRequest():null );
    
    for( int i=0; i < me.getValue().getSplit(); i++ )
     dtbl[level][pos++]=rc;

//    TableData td = new TableData();
//    td.setColspan(me.getValue().getSplit());
//    tbl.add(new LabelField(me.getKey() + "\n" + String.valueOf(me.getValue().getCount())), new TableData());
// tbl.add(new LabelField(String.valueOf(me.getValue().getCount())), new
// TableData());

    drawResult(me.getValue(), level + 1,dtbl);

   }
   
   if( anysc != null )
   {
    ReportCell rc = new ReportCell(anysc, "[ANY]", request.getReportRequestItems().get(level),
      level!=0?dtbl[level-1][pos].getRequest():null );
    for( int i=0; i < anysc.getSplit(); i++ )
     dtbl[level][pos++]=rc;
    
    drawResult(anysc, level + 1,dtbl);

   }

  }

 }

 private int countSplit(SimpleCounter c)
 {
  if(c.getRef() == null && c.getRefMap() == null)
  {
   c.setSplit(1);
   return 0;
  }

  if(c.getRef() != null)
  {
   int l = countSplit(c.getRef());
   c.setSplit(c.getRef().getSplit());
   return l + 1;
  }
  else
  {
   int sum = 0;

   int l = 0;
   for(SimpleCounter sc : c.getRefMap().values())
   {
    int k = countSplit(sc);

    if(k > l)
     l = k;

    sum += sc.getSplit();
   }

   c.setSplit(sum);
   return l + 2;
  }

 }
 
 class OrientationListener extends ButtonListenerAdapter
 {
  private ToolbarButton v,h;
  
  public OrientationListener(ToolbarButton tbbv, ToolbarButton tbbh )
  {
   v=tbbv;
   h=tbbh;
  }

  public void onClick(Button button, EventObject e)
  {
   if("V".equals(button.getStateId()))
   {
    button.setDisabled(true);
    h.setDisabled(false);
    
    remove(table, true);
    
    table = new Panel();
    table.setStyle("font-size: "+zoom+"pt");
    table.addClass("tablePanel");
   
    table.setLayout( new TableLayout(header.size()) );
    
    ReportCell[][] dtbl = new ReportCell[header.size()][];
    
    int i = 0;
    for(String o : header)
    {
     dtbl[i] = new ReportCell[chain.getSplit() + 1];
     
     final ReportCell rc = new ReportCell(0);
     rc.setHTML(o);
     
     dtbl[i][0]=rc;
     i++;
    }
    
    drawResult(chain, 0, dtbl);
    
    renderVerticalTable(dtbl, table);

    add(table);
    doLayout();

    defaultHoriz=false;
    ConfigManager.setConfigParameter(CFG_ORNT_KEY, Boolean.toString(defaultHoriz));
   }
   else if("H".equals(button.getStateId()))
   {
    button.setDisabled(true);
    v.setDisabled(false);

    remove(table, true);
    
    table = new Panel();
    table.setStyle("font-size: "+zoom+"pt");
    table.addClass("tablePanel");
   
    add(table);
    
    table.removeAll(true);
    table.setLayout( new TableLayout(chain.getSplit()+1) );
    
    ReportCell[][] dtbl = new ReportCell[header.size()][];
    
    int i = 0;
    for(String o : header)
    {
     dtbl[i] = new ReportCell[chain.getSplit() + 1];
     
     final ReportCell rc = new ReportCell(0);
     rc.setHTML(o);
     
     dtbl[i][0]=rc;
     i++;
    }
    
    drawResult(chain, 0, dtbl);
    
    renderHorizontalTable(dtbl, table);

    doLayout();

    defaultHoriz=true;
    ConfigManager.setConfigParameter(CFG_ORNT_KEY, Boolean.toString(defaultHoriz));

   }
   else if("ZO".equals(button.getStateId()))
   {
    if( zoom <= 4 )
     return;
    
    zoom -= 2;
    table.setStyle("font-size: "+zoom+"pt");
    
    ConfigManager.setConfigParameter(CFG_SIZE_KEY, Integer.toString(zoom) );
   }
   else if("ZI".equals(button.getStateId()))
   {
    if( zoom >= 60 )
     return;
    
    zoom += 2;
    table.setStyle("font-size: "+zoom+"pt");

    ConfigManager.setConfigParameter(CFG_SIZE_KEY, Integer.toString(zoom) );
   }
  }
 }
}
