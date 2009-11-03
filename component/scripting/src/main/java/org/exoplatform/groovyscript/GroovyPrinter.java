/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.groovyscript;

import org.exoplatform.commons.utils.Text;

import java.io.IOException;
import java.io.Writer;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class GroovyPrinter extends Writer
{

   public final void println(Object o)
   {
      print(o);
      println();
   }

   public final void println()
   {
      try
      {
         write('\n');
      }
      catch (IOException ignore)
      {
      }
   }

   public final void print(Object o)
   {
      try
      {
         if (o == null)
         {
            write("null");
         }
         else if (o instanceof Text)
         {
            ((Text)o).writeTo(this);
         }
         else
         {
            write(o.toString());
         }
      }
      catch (IOException ignore)
      {
      }
   }
}
