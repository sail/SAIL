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

package uk.ac.ebi.sail.client;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.Projection;
import uk.ac.ebi.sail.client.common.Qualifier;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.common.Variable;
import uk.ac.ebi.sail.client.common.Variable.Type;

public class TestDataManager //implements ClassifierManager
{
 static TestDataManager instance;
 
 List<Classifier> classifierList = new ArrayList<Classifier>(30);
 List<Projection> projList = new ArrayList<Projection>(5);
 final List<Parameter> params = new ArrayList<Parameter>(10);
 
 public static TestDataManager getInstance()
 {
  if( instance == null )
   instance=new TestDataManager();
  
  return instance;
 }
 
 TestDataManager()
 {
  int id=1;
  
  Classifier cl_A = new Classifier();
  cl_A.setName("A"); //$NON-NLS-1$
  cl_A.setAllowMulty(true);
  cl_A.addTag( new Tag("A-a") ); //$NON-NLS-1$
  cl_A.addTag( new Tag("A-b") ); //$NON-NLS-1$
  cl_A.addTag( new Tag("A-c") ); //$NON-NLS-1$

  Classifier cl_B = new Classifier();
  cl_B.setName("B"); //$NON-NLS-1$
  cl_B.setAllowMulty(false);
  cl_B.addTag( new Tag("B-a") ); //$NON-NLS-1$
  cl_B.addTag( new Tag("B-b") ); //$NON-NLS-1$
  cl_B.addTag( new Tag("B-c") ); //$NON-NLS-1$
  
  Classifier cl_C = new Classifier();
  cl_C.setName("C"); //$NON-NLS-1$
  cl_C.setDescription("<B>\"C\"</B>-class classifier"); //$NON-NLS-1$
  cl_C.setAllowMulty(false);
  cl_C.addTag( new Tag("C-a") ); //$NON-NLS-1$
  cl_C.addTag( new Tag("C-b") ); //$NON-NLS-1$
  cl_C.addTag( new Tag("C-c") ); //$NON-NLS-1$

  Classifier onthology = new Classifier();
  onthology.setName("Onthology"); //$NON-NLS-1$
  onthology.setAllowMulty(true);
  Tag p3g = new Tag("P3G"); //$NON-NLS-1$
  onthology.addTag( p3g );
  Tag engage = new Tag("ENGAGE"); //$NON-NLS-1$
  onthology.addTag( engage );

  Classifier knDomain = new Classifier();
  knDomain.setName("Knowledge domain"); //$NON-NLS-1$
  knDomain.setAllowMulty(true);
  Tag sysBio = new Tag("System biology"); //$NON-NLS-1$
  sysBio.setDescription("System biology related"); //$NON-NLS-1$
  knDomain.addTag( sysBio );
  Tag clinic = new Tag("Clinical trials"); //$NON-NLS-1$
  knDomain.addTag( clinic );

  Classifier target = new Classifier();
  target.setName("Target"); //$NON-NLS-1$
  target.setAllowMulty(false);
  Tag clcl = new Tag("Classifier"); //$NON-NLS-1$
  target.addTag( clcl );
  Tag clpr = new Tag("Parameter"); //$NON-NLS-1$
  target.addTag( clpr );
  Tag clrel = new Tag("Relation"); //$NON-NLS-1$
  target.addTag( clrel );

  Classifier rel = new Classifier();
  rel.setName("Type"); //$NON-NLS-1$
  rel.setAllowMulty(false);
  Tag relSyn = new Tag("Synonym"); //$NON-NLS-1$
  rel.addTag( relSyn );
  Tag relrel = new Tag("Related"); //$NON-NLS-1$
  rel.addTag( relrel );

  Classifier std = new Classifier();
  std.setName("Classifier Standard"); //$NON-NLS-1$
  std.setAllowMulty(false);
  Tag stdstd = new Tag("Standard"); //$NON-NLS-1$
  std.addTag( stdstd );
  Tag stdNstd = new Tag("Not standard"); //$NON-NLS-1$
  std.addTag( stdNstd );

  
  cl_A.addClassificationTag(clpr);
  cl_B.addClassificationTag(clpr);
  cl_C.addClassificationTag(clpr);
  onthology.addClassificationTag(clpr);
  knDomain.addClassificationTag(clpr);
  
  rel.addClassificationTag(clrel);
  target.addClassificationTag(clcl);
  std.addClassificationTag(clcl);
  
  cl_A.addClassificationTag(stdNstd);
  cl_B.addClassificationTag(stdNstd);
  cl_C.addClassificationTag(stdNstd);
  onthology.addClassificationTag(stdstd);
  knDomain.addClassificationTag(stdstd);
  rel.addClassificationTag(stdstd);
  target.addClassificationTag(stdstd);
  std.addClassificationTag(stdstd);

  
  classifierList.add(target);
  classifierList.add(std);
  classifierList.add(cl_A);
  classifierList.add(cl_B);
  classifierList.add(cl_C);
  classifierList.add(onthology);
  classifierList.add(knDomain);
  classifierList.add(rel);

  Projection pr = new Projection();
  pr.setName("Target"); //$NON-NLS-1$
  pr.addClassifier(classifierList.get(0));
  pr.addClassifier(classifierList.get(1));
  projList.add(pr);

  pr = new Projection();
  pr.setName("Standard"); //$NON-NLS-1$
  pr.addClassifier(classifierList.get(1));
  pr.addClassifier(classifierList.get(0));
  projList.add(pr);

  pr = new Projection();
  pr.setName("Onthology"); //$NON-NLS-1$
  pr.addClassifier(classifierList.get(5));
  pr.addClassifier(classifierList.get(6));
  projList.add(pr);

  pr = new Projection();
  pr.setName("Domain"); //$NON-NLS-1$
  pr.addClassifier(classifierList.get(6));
  pr.addClassifier(classifierList.get(5));
  projList.add(pr);

  Parameter bp = new Parameter();
  bp.setCode("BSP"); //$NON-NLS-1$
  bp.setName("Base"); //$NON-NLS-1$
  bp.setDescription("Base parameter for others"); //$NON-NLS-1$

  Variable v = new Variable();
  v.setId(id++);
  v.setName("BaseV"); //$NON-NLS-1$
  v.setType(Type.INTEGER);
  bp.addVariable(v);
  
  Qualifier q = new Qualifier();
  q.setId(id++);
  q.setName("BaseQ"); //$NON-NLS-1$
  bp.addQualifier(q);

  int N=5;
  
  for( int i=1; i <= N; i++)
  {
   Parameter p = new Parameter();
   p.setCode("PC"+i); //$NON-NLS-1$
   p.setName("P"+i); //$NON-NLS-1$
   p.setDescription("Description of parameter CODE=PC"+i); //$NON-NLS-1$
   params.add( p );
  }
  
  params.get(1).setName("AP2"); //$NON-NLS-1$
  
  v = new Variable();
  v.setId(id++);
  v.setName("V-2"); //$NON-NLS-1$
  v.setType(Type.INTEGER);
  params.get(2).addVariable( v );

  v = new Variable();
  v.setId(id++);
  v.setName("V-3"); //$NON-NLS-1$
  v.setType(Type.INTEGER);
  params.get(3).addVariable( v );

  q = new Qualifier();
  q.setId(id++);
  q.setName("Q-2"); //$NON-NLS-1$
  params.get(2).addQualifier(q);

  q = new Qualifier();
  q.setId(id++);
  q.setName("Q-3"); //$NON-NLS-1$
  params.get(3).addQualifier(q);

  params.get(2).addInheritedParameter(bp);
  params.get(3).addInheritedParameter(bp);
  
  params.get(0).addClassificationTag(p3g);
  params.get(0).addClassificationTag(engage);
  params.get(0).addClassificationTag(sysBio);

  params.get(1).addClassificationTag(engage);
  params.get(1).addClassificationTag(sysBio);
  
  params.get(2).addClassificationTag(engage);
  params.get(2).addClassificationTag(clinic);

  params.get(3).addClassificationTag(p3g);
  params.get(3).addClassificationTag(clinic);
  
  params.add(bp);
 }

 public List<Parameter> getParameters()
 {
  return params;
 }
 
 public List<Classifier> getClassifiers()
 {
  return classifierList;
 }
 
 public Projection getDefaultProjection()
 {
  return projList.get(1);
 }
 
 List<Projection> getProjections()
 {
  return projList;
 }
}
