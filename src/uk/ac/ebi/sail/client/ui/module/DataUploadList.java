package uk.ac.ebi.sail.client.ui.module;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.EnumerationSelectListener;
import uk.ac.ebi.sail.client.FilteredEnumeration;
import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.ProjectionManager;
import uk.ac.ebi.sail.client.common.Annotation;
import uk.ac.ebi.sail.client.common.ComplexFilter;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ParameterFormat;
import uk.ac.ebi.sail.client.common.Qualifier;
import uk.ac.ebi.sail.client.common.RequestItem;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.common.Study;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.common.Variable;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.ActionFlags;
import uk.ac.ebi.sail.client.ui.ActionHelper;
import uk.ac.ebi.sail.client.ui.ObjectSelectionListener;
import uk.ac.ebi.sail.client.ui.RequestItemSelectListener;
import uk.ac.ebi.sail.client.ui.StudyCollectionStateListener;
import uk.ac.ebi.sail.client.ui.module.ObjectList.Selection;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.PaddedPanel;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;


public class DataUploadList extends Panel{

	 public DataUploadList( )
	 {
	  this(null);
	 }

	public DataUploadList( Action[] button) {
		super();
        setAutoScroll(true);
		setTitle("Upload");
		setIconCls("tab-icon");
		setClosable(false);
		setAutoWidth(true);
		
		BorderLayoutData centerData = new BorderLayoutData(RegionPosition.CENTER);  
		centerData.setMinSize(175);  
		centerData.setMargins(new Margins(0, 5, 0, 0));  
		
		Action[] btns=new Action[]{
				new Action("Login","login"+ActionFlags.separator+ActionFlags.ALLOW_MULTIPLE,"paramExport", null)
		};
		
		if( button != null )
			   btns = ActionHelper.mergeActions(btns,button);
		
		
		
//		PaddedPanel pp = new PaddedPanel();
	//	  pp.setBorder(true);
		//  pp.setLayout( new FitLayout() );
		 // add(pp, centerData);

		
		// TODO Auto-generated constructor stub
	}
	
}
