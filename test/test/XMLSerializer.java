package test;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import uk.ac.ebi.sail.client.common.ComplexFilter;
import uk.ac.ebi.sail.client.common.IntRange;

import com.pri.util.stream.StringInputStream;


public class XMLSerializer
{

 /**
  * @param args
  */
 public static void main(String[] args)
 {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  
  XMLEncoder enc = new XMLEncoder(baos);
  
  ComplexFilter cf = new ComplexFilter();
  
//  cf.addIntRange( new IntRange(100, 1001, 1002));
//  cf.addIntRange( new IntRange(101, 1101, 1102));
//  cf.addRealRange( new Range(103, 2001, 2002));
//  cf.addVariant(104, 5);
//  cf.addVariant(104, 6);
  cf.addVariant(105, 7);
 
  IntRange irng = new IntRange(2, 3, 4);
  
  enc.writeObject(cf);
  
  enc.close();
  
  String s;
  try
  {
   s = new String(baos.toByteArray(),"utf-8");
   System.out.println( s );
  }
  catch(UnsupportedEncodingException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
   return;
  }
  
  XMLDecoder dec = new XMLDecoder( new StringInputStream(s) );
  
  Object o = dec.readObject();
  
  dec.close();
 }

}
