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

package uk.ac.ebi.sail.client.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.ebi.sail.client.common.Classifier.Target;
import uk.ac.ebi.sail.client.common.TmpParamRef.Rel;
import uk.ac.ebi.sail.client.common.Variable.Type;

public class ParameterFormat
{
 private static final String LN_CODE = "Code";
 private static final String LN_PARAMETER = "Parameter";
 private static final String LN_NAME = "Name";
 private static final String LN_TYPE = "Type";
 private static final String LN_INHERIT = "Inherit";
 private static final String LN_DESC = "Description";
 private static final String LN_VARIABLE = "Variable";
 private static final String LN_QUALIFIER = "Qualifier";
 private static final String LN_VALUES = "Variants";
 private static final String LN_VARIANT = "Variant";
 private static final String LN_TAG = "Tag";
 private static final String LN_RELATION = "Relation";
 private static final String LN_PREDEFINED = "Predefined";
 private static final String LN_MANDATORY = "Mandatory";
 private static final String LN_ANNOTATION = "Annotation";
 
 private static void validateParameter(Parameter p, int lineNumber, Collection<Classifier> mndClsfr) throws ParseException
 {
  if( p.getName() == null )
   throw new ParseException(lineNumber,"Parameter must have non empty name");
  
  if( p.getVariables() != null )
  {
   for( Variable v : p.getVariables() )
   {
    if( v.getType() == null )
     throw new ParseException(lineNumber,"Variable '"+v.getName()+"' has no type");
   }
  }
  
  if( mndClsfr.size() > 0 && (p.getClassificationTags() == null || p.getClassificationTags().size() == 0 ) )
   throw new ParseException(lineNumber,"There are mandatory classifiers and parameter '"+p.getCode()+"' has no tags of them");
   
  for(Classifier mcl : mndClsfr )
  {
   int found = 0;
   
   for( Tag t : p.getClassificationTags() )
   {
    if( t.getClassifier().getId() == mcl.getId() )
     found++;
   }
   
   if( mcl.isMandatory() && found == 0 )
    throw new ParseException(lineNumber,"There are mandatory classifiers and parameter '"+p.getCode()+"' has no tags of them ("+mcl.getName()+")");
   
   if( ! mcl.isAllowMulty() && found > 1 )
    throw new ParseException(lineNumber,"Classifier ("+mcl.getName()+") don't allow multiple tags for one parameter ("+p.getCode()+")");
  }
 
 }
 
 public static List<Parameter> parse( String txt, Collection<Parameter> params, Collection<Classifier> classifiersList, Collection<Tag> tags ) throws ParseException
 {

  String SEP = "\t";
  
  int cpos = 0;
  int len = txt.length();
  
  String lineSep = "\n";
  
  cpos = txt.indexOf(lineSep);
  
  if( cpos == -1 )
   throw new ParseException(1,"File must contain at least one line");
  
  if( cpos > 0 && txt.charAt(cpos-1) == '\r' )
   lineSep="\r\n";
  
  cpos = 0;
  
  List<TmpParamRef> tmpPList = new ArrayList<TmpParamRef>(50);
  List<Tag> commonTags = new ArrayList<Tag>(5);
  
  
  Collection<Classifier> mndClsfr = new ArrayList<Classifier>();
  for( Classifier cl : classifiersList )
  {
   if( cl.isMandatory() && cl.getTarget() == Target.PARAMETER )
    mndClsfr.add(cl);
  }
  
  Parameter cp = null;
  Qualifier cq = null;
  Variable  cv = null;
  TmpParamRef cref=null;
  
  int ln=0;
  int paramLine=0;
  while(cpos < len)
  {
   ln++;
   int pos = txt.indexOf(lineSep, cpos);

   String line;
   if(pos == -1)
   {
    if( cpos == len-1 )
     break;
    
    line=txt.substring(cpos);
    pos=txt.length();
   }
   else
    line =txt.substring(cpos, pos);
   
   cpos = pos + lineSep.length();
   
   if( line.startsWith(SEP) )
    continue;
   
   line = line.trim();
   
   if( line.startsWith("//") || line.startsWith("--") )
    continue;
   
   String[] parts = line.split(SEP);
   
   for( int j=0; j < parts.length; j++ )
   {
    if( parts[j].length() >= 2 && parts[j].charAt(0)=='"' && parts[j].charAt(parts[j].length()-1) == '"')
     parts[j] = parts[j].substring(1,parts[j].length()-1);
   }
   
   if( parts == null || parts.length < 2 )
    continue;
   
   if( LN_CODE.equalsIgnoreCase(parts[0]) || LN_PARAMETER.equalsIgnoreCase(parts[0])  )
   {
    
    for( Parameter p : params )
    {
     if( p.getCode().equals(parts[1]) )
      throw new ParseException(ln,"Parameter with code '"+parts[1]+"' already exists in SAIL. ID="+p.getId());
    }
    
   
    if( cref != null )
    {
     if( cp.getDescription() == null )
      cp.setDescription(cp.getName());
     
     validateParameter(cp, paramLine, mndClsfr);
     
     tmpPList.add( cref );
    }
    
    for( TmpParamRef tp : tmpPList )
    {
     if( tp.getParameter().getCode().equals(parts[1]) )
      throw new ParseException(ln,"Parameter with code '"+parts[1]+"' already exists in import file.");
    }

    paramLine = ln;
    cp = new Parameter();
    cref = new TmpParamRef(cp);
    cp.setCode(parts[1]);
    
    if( commonTags.size() > 0 )
    {
     for( Tag t : commonTags )
      cp.addClassificationTag(t);
    }
    
    cv=null;
    cq=null;
    
   }
   else if( LN_NAME.equals(parts[0]))
   {
    if( cp.getName() != null )
     throw new ParseException(ln,"Multiple '"+LN_NAME+"' lines are not allowed");
    
    cp.setName(parts[1]);
   }
   else if( LN_DESC.equals(parts[0]))
   {
    String desc = parts[1].replaceAll("\\\\n", "\n");
    
    if( cv != null )
    {
     if( cv.getDescription() != null )
      cv.setDescription(cv.getDescription()+'\n'+desc);
     else
      cv.setDescription(desc);
    }
    else if( cq != null )
    {
     if( cq.getDescription() != null )
      cq.setDescription(cq.getDescription()+'\n'+desc);
     else
      cq.setDescription(desc);
    }
    else
    {
     if( cp.getDescription() != null )
      cp.setDescription(cp.getDescription()+'\n'+desc);
     else
      cp.setDescription(desc);
    }
   }
   else if( LN_ANNOTATION.equals(parts[0]))
   {
    if( parts.length != 4 )
     throw new ParseException(ln, "Invalid annotation line. Must be 3 components");

    String clsName = parts[1];
    String tagName = parts[2];
    String text    = parts[3];
  
    Tag tag = null;
    
    for( Tag t : tags )
    {
     if( t.getName().equals(tagName) && t.getClassifier().getName().equals(clsName) )
     {
      tag = t;
      break;
     }
    }
    
    if( tag == null )
     throw new ParseException(ln, "Tag '"+parts[1]+":"+parts[2]+"' doesn't exist");

    if( tag.getClassifier().getTarget() != Target.PARAMETER_ANN )
     throw new ParseException(ln, "Tag must be pertaining to PARAMETER_ANN classifier");
    
    text = text.replaceAll("\\n", "\n");
    
    if( cp == null )
     throw new ParseException(ln, "Invalid context for Annotation line");
    
    Annotation a = null;
    
    if( cp.getAnnotations() != null )
    {
     for( Annotation ann : cp.getAnnotations() )
     {
      if( ann.getTag().getId() == tag.getId() )
      {
       a = ann;
       break;
      }
     }
    }
    
    if( a == null )
    {
     a=new Annotation();
     a.setTag(tag);
     a.setText(text);
     
     cp.addAnnotation(a);
    }
    else
     a.setText(a.getText()+'\n'+text);
   }
   else if( LN_RELATION.equals(parts[0]))
   {
    if( cp == null )
     throw new ParseException(ln,"Invalid context from Relation");
    
    Tag tag = null;
    
    if( parts.length != 4 )
     throw new ParseException(ln, "Invalid Relation line");
    
    String clsName = parts[1];
    String tagName = parts[2];
    String prmCode = parts[3];
  
    for( Tag t : tags )
    {
     if( t.getName().equals(tagName) && t.getClassifier().getName().equals(clsName) )
     {
      tag = t;
      break;
     }
    }
    
    if( tag == null )
     throw new ParseException(ln, "Tag '"+parts[1]+":"+parts[2]+"' doesn't exist");

    if( tag.getClassifier().getTarget() != Target.RELATION )
     throw new ParseException(ln, "Tag must be pertaining to RELATION classifier");
    
    
    cref.addRelation(prmCode, tag, ln);
   }
   else if( LN_INHERIT.equals(parts[0]) )
   {
    cref.addInherit(parts[1],ln);
   }
   else if( LN_VARIABLE.equals(parts[0]) )
   {
    if( cp.getVariables() != null )
    {
     for( Variable v : cp.getVariables() )
     {
      if(v.getName().equals(parts[1]) )
       throw new ParseException(ln,"Variable with name '"+parts[1]+"' already exists in parameter '"+cp.getCode()+"'");
     }
    }
    
    if( cp.getQualifiers() != null )
    {
     for( Qualifier q : cp.getQualifiers() )
     {
      if(q.getName().equals(parts[1]) )
       throw new ParseException(ln,"Qualifier with name '"+parts[1]+"' already exists in parameter '"+cp.getCode()+"'");
     }
    }

    
    cv = new Variable();
    cv.setName(parts[1]);
    cp.addVariable(cv);
    
    cq=null;
   }
   else if( LN_TYPE.equals(parts[0]) )
   {
    if( cv == null )
     throw new ParseException(ln, "Invalid context for '"+LN_TYPE+"'");
    
    Variable.Type typ = null;
    
    try
    {
     typ = Variable.Type.valueOf(parts[1]);
    }
    catch (Exception e) 
    {
    }
    
    if( typ == null )
     throw new ParseException(ln, "Invalid variable type '"+parts[1]+"'");
    
    cv.setType( typ );
   }
   else if( LN_QUALIFIER.equals(parts[0]) )
   {
    if( cp.getQualifiers() != null )
    {
     for( Qualifier q : cp.getQualifiers() )
     {
      if(q.getName().equals(parts[1]) )
       throw new ParseException(ln,"Qualifier with name '"+parts[1]+"' already exists in parameter '"+cp.getCode()+"'");
     }
    }

    if( cp.getVariables() != null )
    {
     for( Variable v : cp.getVariables() )
     {
      if(v.getName().equals(parts[1]) )
       throw new ParseException(ln,"Variable with name '"+parts[1]+"' already exists in parameter '"+cp.getCode()+"'");
     }
    }

    cv=null;
    cq= new Qualifier();
    cq.setName(parts[1]);
    cq.setMandatory(true);
    cp.addQualifier(cq);
   }
   else if( LN_VARIANT.equals(parts[0]) )
   {
    int varInc = Integer.MIN_VALUE;
    
    if( parts.length == 3 )
    {
     try
     {
      varInc = Integer.parseInt(parts[2]);
     }
     catch (Exception e) {
      throw new ParseException(ln, "Invalid variant encoding: "+parts[2]);
     }
    }
    
    List<Variant> varis = null;
    
    if( cv != null )
    {
     if( cv.getType() != Variable.Type.ENUM )
      throw new ParseException(ln,"Variable must by 'ENUM' type");

     varis = cv.getVariants();
    }
    else if( cq != null )
     varis = cq.getVariants();
    else
     throw new ParseException(ln, "Invalid context for Variant line");
    
    if( varInc == Integer.MIN_VALUE )
    {
     if(varis == null || varis.size() == 0)
      varInc = 1;
     else
     {
      int max = Integer.MIN_VALUE;

      for(Variant vr : varis)
       if(vr.getCoding() > max)
        max = vr.getCoding();

      varInc = max + 1;
     }
    }
    else
    {
     if(varis != null && varis.size() != 0)
     {
      for(Variant vr : varis)
      {
       if(vr.getCoding() == varInc)
        throw new ParseException(ln, "Duplicate variant coding: " + varInc);
      }
     }
    }
     
    if( cv != null )
     cv.addVariant( new Variant(parts[1],varInc, true) );
    else
     cq.addVariant( new Variant(parts[1],varInc, true) );
   }
   else if( LN_VALUES.equals(parts[0]) )
   {
    int vc=1;
    for( int i=1; i<parts.length; i++ )
    {
     if( parts[i] == null || parts[i].length() == 0 )
      break;
     
     int espos = parts[i].indexOf("=");
     
     int vcod = vc++;
     if( espos != -1 )
     {
      parts[i] = parts[i].substring(0,espos).trim();
      try
      {
       vcod = Integer.parseInt(parts[i].substring(espos+1).trim());
      }
      catch (Exception e)
      {
      }
     }
     
     
     if( cv != null )
      cv.addVariant( new Variant(parts[i],vcod, true) );
     else
      cq.addVariant(new Variant(parts[i],vcod, true));
     
    }
   }
   else if( LN_TAG.equals(parts[0]) )
   {
    Tag tag = null;

    if( parts.length != 3 )
     throw new ParseException(ln, "Invalid Tag line");
    
    String clsName = parts[1];
    String tagName = parts[2];
  
    for( Tag t : tags )
    {
     if( t.getName().equals(tagName) && t.getClassifier().getName().equals(clsName) )
     {
      tag = t;
      break;
     }
    }
    
    if( tag == null )
     throw new ParseException(ln, "Tag '"+parts[1]+":"+parts[2]+"' doesn't exist");

    if( cp != null )
     cp.addClassificationTag(tag);
    else
     commonTags.add(tag);
   }
   else if( LN_PREDEFINED.equals(parts[0]))
   {
    boolean ispred = false;
    
    if( "true".equalsIgnoreCase(parts[1]) || "yes".equalsIgnoreCase(parts[1]) || "1".equalsIgnoreCase(parts[1]) )
     ispred = true;
    else if( "false".equalsIgnoreCase(parts[1]) || "no".equalsIgnoreCase(parts[1]) || "0".equalsIgnoreCase(parts[1]) )
     ispred = false;
    else
     throw new ParseException(ln,"Invalid value for '"+LN_PREDEFINED+"' line. Must be one of true/false yes/no or 1/0");
    
    if( cq != null )
     cq.setPredefined(ispred);
    else if( cv != null )
    {
     if( cv.getType() != Variable.Type.ENUM )
      throw new ParseException(ln, "Type of variable must be ENUM to have predefined values");
     
     cv.setPredefined(ispred);
    }
    else
     throw new ParseException(ln,"Invalid context for '"+LN_PREDEFINED+"' line");
   }
   else if( LN_MANDATORY.equals(parts[0]))
   {
    boolean ispred = false;
    
    if( "true".equalsIgnoreCase(parts[1]) || "yes".equalsIgnoreCase(parts[1]) || "1".equalsIgnoreCase(parts[1]) )
     ispred = true;
    else if( "false".equalsIgnoreCase(parts[1]) || "no".equalsIgnoreCase(parts[1]) || "0".equalsIgnoreCase(parts[1]) )
     ispred = false;
    else
     throw new ParseException(ln,"Invalid value for '"+LN_MANDATORY+"' line. Must be one of true/false yes/no or 1/0");

    if( cq != null )
     cq.setMandatory(ispred);
    else
     throw new ParseException(ln,"Invalid context for '"+LN_MANDATORY+"' line");
   }
   else if( parts[0].length() > 0 )
    throw new ParseException(ln,"Invalid string: "+parts[0]+" -> "+parts[1]);
    


  }
  
  if( cref != null )
  {
   if( cp.getDescription() == null )
    cp.setDescription(cp.getName());
   
   validateParameter(cp, paramLine, mndClsfr);
   tmpPList.add( cref );
  }

  List<Parameter> plst = new ArrayList<Parameter>(tmpPList.size());
  
  for( TmpParamRef ttp : tmpPList )
  {
   Parameter param =  ttp.getParameter();
   
   if( ttp.getInherits() != null )
   {
    for(TmpParamRef.Inh ir : ttp.getInherits())
    {
     Parameter ip = null;
     String pName = ir.getParameter();
     for(TmpParamRef atp : tmpPList)
     {
      if(atp.getParameter().getCode().equals(pName))
      {
       ip = atp.getParameter();
       break;
      }
     }

     if(ip == null)
     {
      for(Parameter ap : params)
      {
       if(ap.getCode().equals(pName))
       {
        ip = ap;
        break;
       }
      }
     }
     
     if( ip == null )
      throw new ParseException(ir.getLineNumber(),"Invalid inherited parameter reference: '"+pName+"'");

     Parameter res = checkHierarchy(param,ip);
     
     if( res != null )
      throw new ParseException(ir.getLineNumber(),"Parameter '"+ip.getCode()+"' is already in hierarchy of parameter '"+param.getCode()+"'");

     res = checkHierarchy(ip,param);
     
     if( res != null )
      throw new ParseException(ir.getLineNumber(),"Parameter '"+ip.getCode()+"' has parameter '"+param.getCode()+"' in its hierarchy");
     
     param.addInheritedParameter(ip);
    }
   }
   
   if( ttp.getRelations() != null )
   {
    for(Rel rl : ttp.getRelations())
    {
     Parameter rp = null;

     String pName = rl.getParameterCode();

     for(TmpParamRef atp : tmpPList)
     {
      if(atp.getParameter().getCode().equals(pName))
      {
       rp = atp.getParameter();
       break;
      }
     }

     if(rp == null)
     {
      for(Parameter ap : params)
      {
       if(ap.getCode().equals(pName))
       {
        rp = ap;
        break;
       }
      }
     }

     if(rp == null)
      throw new ParseException(rl.getLineNumber(), "Invalid parameter relation reference: '" + pName + "'");
     
     Relation r = new Relation();
     r.setHostParameter(param);
     r.setTargetParameter(rp);
     r.setTag(rl.getTag());
     
     param.addRelation(r);
    }
   }
   
   
   plst.add(param);
  }
  
  return plst;
 
 }
 
 
 private static Parameter checkHierarchy( Parameter newParam, Parameter base )
 {
  if( newParam.getInheritedParameters() == null )
   return null;
  
  for( Parameter inh : newParam.getInheritedParameters() )
  {
   if( base == inh )
    return newParam;
   
   Parameter res = checkHierarchy(inh, base);
   if( res != null )
    return res;
  }
  
  return null;
 }
 
 public static String export(Collection<Parameter> params, String delim, String eol )
 {
  StringBuilder sb = new StringBuilder();
  
  
  for(Parameter p : params )
  {
   sb.append(eol);
   
   sb.append(LN_PARAMETER).append(delim).append(p.getCode()).append(delim).append(delim).append(eol);
   sb.append(LN_NAME).append(delim).append(p.getName()).append(delim).append(delim).append(eol);
   
   appendDescription(p.getDescription(), sb, delim, eol);
   
   if( p.getInheritedParameters() != null )
   {
    for( Parameter ip : p.getInheritedParameters() )
    {
     sb.append(LN_INHERIT).append(delim).append(ip.getCode()).append(delim).append(delim).append(eol);
    }
   }
   
   if( p.getClassificationTags() != null )
   {
    for( Tag t : p.getClassificationTags() )
    {
     sb.append(LN_TAG).append(delim).append(t.getClassifier().getName()).append(delim).append(t.getName()).append(delim).append(eol);
    }
   }
   
   if( p.getAnnotations() != null )
   {
    for( Annotation an : p.getAnnotations() )
    {
     sb.append(LN_ANNOTATION).append(delim).append(an.getTag().getClassifier().getName()).append(delim)
     .append(an.getTag().getName()).append(delim).append(an.getText().replaceAll("\n", "\\n")).append(delim).append(eol);
    }
   }
 
   if( p.getRelations() != null )
   {
    for( Relation r : p.getRelations() )
    {
     sb.append(LN_RELATION).append(delim)
     .append(r.getTag().getClassifier().getName()).append(delim)
     .append(r.getTag().getName()).append(delim)
     .append(r.getTargetParameter().getCode()).append(eol);
    }
   }

   if( p.getVariables() != null )
   {
    for( Variable v : p.getVariables() )
    {
     sb.append(LN_VARIABLE).append(delim).append(v.getName()).append(delim).append(delim).append(eol);
     sb.append(LN_TYPE).append(delim).append(v.getType().name()).append(delim).append(delim).append(eol);
     
     appendDescription(v.getDescription(), sb, delim, eol);
     
     if( v.getType() == Type.ENUM )
     {
      sb.append(LN_PREDEFINED).append(delim).append(v.isPredefined()?"YES":"NO").append(delim).append(delim).append(eol);
      
      if( v.getVariants() != null )
      {
       for( Variant var : v.getVariants() )
       {
        if( var.isPredefined() )
         sb.append(LN_VARIANT).append(delim).append(var.getName()).append(delim).append(var.getCoding()).append(delim).append(eol);
       }
      }
     }
    }
   }
  
   if( p.getQualifiers() != null )
   {
    for(Qualifier q : p.getQualifiers())
    {
     sb.append(LN_QUALIFIER).append(delim).append(q.getName()).append(delim).append(delim).append(eol);

     appendDescription(q.getDescription(), sb, delim, eol);

     sb.append(LN_PREDEFINED).append(delim).append(q.isPredefined() ? "YES" : "NO").append(delim).append(delim).append(eol);
     sb.append(LN_MANDATORY).append(delim).append(q.isMandatory() ? "YES" : "NO").append(delim).append(delim).append(eol);

     if(q.getVariants() != null)
     {
      for(Variant var : q.getVariants())
      {
       if( var.isPredefined() )
        sb.append(LN_VARIANT).append(delim).append(var.getName()).append(delim).append(var.getCoding()).append(delim).append(eol);
      }
     }
    }
   }

  }
    
  return sb.toString();
 }
 
 private static void appendDescription( String str, StringBuilder sb, String delim, String eol  )
 {
  if( str == null )
   return;
  
  if( str.indexOf('\n') == -1 )
   sb.append(LN_DESC).append(delim).append(str).append(delim).append(delim).append(eol);
  else
  {
   int cpos=0;
   while( true )
   {
    int pos = str.indexOf('\n', cpos);
    
    if( pos != -1 )
     sb.append(LN_DESC).append(delim).append(str.substring(cpos, pos)).append(delim).append(delim).append(eol);
    else
    {
     sb.append(LN_DESC).append(delim).append(str.substring(cpos)).append(delim).append(delim).append(eol);
     break;
    }
    
    cpos=pos+1;
   }
  }
 }
}
