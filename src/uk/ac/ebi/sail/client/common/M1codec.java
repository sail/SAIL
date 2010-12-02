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

public class M1codec
{

 private M1codec()
 {
 }
 
 public static String encode( String str, String escChars )
 {
  int pos = 0;
  char ch = 0;
  boolean highMode = false;

  while(pos < str.length())
   if(!isCharLegal(ch = str.charAt(pos), escChars))
    break;
   else
    pos++;

  if(pos == str.length())
   return str;

  StringBuilder sb = new StringBuilder(str.length() * 3 + 6);

  if(pos > 0)
   sb.append(str.substring(0, pos));

  do
  {
   ch = str.charAt(pos);
   if(!isCharLegal(ch, escChars))
   {
    if(ch < 0xFF)
    {
     if( highMode )
      appendHihgModChar(sb,ch);
     else
      sb.append('$').append((char)('A' + (ch >> 4 & 0x0F))).append((char)('A' + (ch & 0x0F)));
    }
    else
    {
     if(!highMode)
      sb.append('`');
     appendHihgModChar(sb,ch);
     highMode=true;
    }
   }
   else
   {
    if(highMode)
     sb.append('`');

    highMode = false;

    sb.append(ch);
   }
   pos++;
  } while(pos < str.length());
  
  return sb.toString();
 }

 public static String decode(String str)
 {
  if( str == null )
   return null;
  
  int pos1 = str.indexOf('$');
  int pos2 = str.indexOf('`');
  int pos;
  boolean highMode = false;

  if(pos1 == -1 && pos2 == -1)
   return str;

  if(pos1 == -1)
   pos = pos2;
  else if(pos2 == -1)
   pos = pos1;
  else
   pos = pos1 < pos2 ? pos1 : pos2;

  StringBuilder sb = new StringBuilder(str.length());

  if(pos > 0)
   sb.append(str.substring(0, pos));

  do
  {
   char ch = str.charAt(pos);

   if(ch == '$')
   {
    sb.append((char) (((str.charAt(pos+1) - 'A') << 4) + (str.charAt(pos+2) - 'A')));
    pos+=2;
   }
   else if(ch == '`')
    highMode = !highMode;
   else
   {
    if(highMode)
    {
     sb.append((char) ((pem_convert_array[ch] << 12)
                     + (pem_convert_array[str.charAt(pos+1)] << 6)
                     +  pem_convert_array[str.charAt(pos+2)]));
     pos+=2;
    }
    else
     sb.append(ch);
   }

   pos++;
  } while(pos < str.length());
  
  return sb.toString();
 }
 
 private static void appendHihgModChar(StringBuilder sb, char ch)
 {
  sb.append(pem_array[(ch >> 12) & 0x3f]);
  sb.append(pem_array[(ch >> 6) & 0x3f]);
  sb.append(pem_array[ch & 0x3f]);
 }

 private static boolean isCharLegal( char ch, String escChars )
 {
  if( ch >= 32 && ch <= 127 && ch != '$' && ch != '`' && (escChars == null || escChars.indexOf(ch) == -1 ) )
   return true;
  
  return false;
 }
 
 private final static char  pem_array[] = { 
  'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', // 0
  'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', // 1
  'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', // 2
  'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', // 3
  'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', // 4
  'o', 'p', 'q', 'r', 's', 't', 'u', 'v', // 5
  'w', 'x', 'y', 'z', '0', '1', '2', '3', // 6
  '4', '5', '6', '7', '8', '9', '_',  '-' // original BASE64 '+', '/' // 7
  };

 private final static byte pem_convert_array[] = new byte[256];

 static 
 {
  for(int i = 0; i < 255; i++)
   pem_convert_array[i] = -1;
  for(int i = 0; i < pem_array.length; i++)
   pem_convert_array[pem_array[i]] = (byte) i;
 }

}
