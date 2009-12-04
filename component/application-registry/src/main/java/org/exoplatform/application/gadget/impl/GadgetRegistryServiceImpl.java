/*
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.application.gadget.impl;

import org.chromattic.api.Chromattic;
import org.chromattic.api.ChromatticSession;
import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.application.registry.mop.ApplicationRegistryChromatticLifeCycle;
import org.exoplatform.commons.chromattic.ChromatticLifeCycle;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.container.xml.ValueParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class GadgetRegistryServiceImpl implements GadgetRegistryService
{

   /** . */
   private static final String DEFAULT_DEVELOPER_GROUP = "/platform/administrators";

   /** . */
   private ChromatticManager chromatticManager;

   /** . */
   private ChromatticLifeCycle chromatticLifeCycle;

   /** . */
   private String gadgetDeveloperGroup;

   /** . */
   private String country;

   /** . */
   private String language;

   /** . */
   private String moduleId;

   /** . */
   private String hostName;

   public GadgetRegistryServiceImpl(ChromatticManager chromatticManager, InitParams params)
   {
      ApplicationRegistryChromatticLifeCycle lifeCycle = (ApplicationRegistryChromatticLifeCycle)chromatticManager.getLifeCycle("app");

      //
      String gadgetDeveloperGroup = null;
      String country = null;
      String language = null;
      String moduleId = null;
      String hostName = null;
      if (params != null)
      {
         PropertiesParam properties = params.getPropertiesParam("developerInfo");
         gadgetDeveloperGroup = properties != null ? properties.getProperty("developer.group") : null;
         ValueParam gadgetCountry = params.getValueParam("gadgets.country");
         country = gadgetCountry != null ? gadgetCountry.getValue() : null;
         ValueParam gadgetLanguage = params.getValueParam("gadgets.language");
         language = gadgetLanguage != null ? gadgetLanguage.getValue() : null;
         ValueParam gadgetModuleId = params.getValueParam("gadgets.moduleId");
         moduleId = gadgetModuleId != null ? gadgetModuleId.getValue() : null;
         ValueParam gadgetHostName = params.getValueParam("gadgets.hostName");
         hostName = gadgetHostName != null ? gadgetHostName.getValue() : null;
      }

      //
      if (gadgetDeveloperGroup == null)
      {
         gadgetDeveloperGroup = DEFAULT_DEVELOPER_GROUP;
      }

      //
      this.country = country;
      this.language = language;
      this.moduleId = moduleId;
      this.hostName = hostName;
      this.gadgetDeveloperGroup  = gadgetDeveloperGroup;
      this.chromatticManager = chromatticManager;
      this.chromatticLifeCycle = lifeCycle;
   }

   public GadgetRegistry getRegistry()
   {
      Chromattic chromattic = chromatticLifeCycle.getChromattic();
      ChromatticSession session = chromattic.openSession();
      GadgetRegistry registry = session.findByPath(GadgetRegistry.class, "gadgets");
      if (registry == null)
      {
         registry = session.insert(GadgetRegistry.class, "gadgets");
      }
      return registry;
   }

   public ChromatticLifeCycle getChromatticLifeCycle()
   {
      return chromatticLifeCycle;
   }

   // ***************


   public Gadget getGadget(String name) throws Exception
   {
      GadgetRegistry registry = getRegistry();

      //
      GadgetDefinition def = registry.getGadget(name);

      //
      return def == null ? null : loadGadget(def);
   }

   public List<Gadget> getAllGadgets() throws Exception
   {
      return getAllGadgets(null);
   }

   public List<Gadget> getAllGadgets(Comparator<Gadget> sortComparator) throws Exception
   {
      GadgetRegistry registry = getRegistry();
      List<Gadget> gadgets = new ArrayList<Gadget>();
      for (GadgetDefinition def : registry.getGadgets())
      {
         Gadget gadget = loadGadget(def);
         gadgets.add(gadget);
      }
      if (sortComparator != null)
      {
         Collections.sort(gadgets, sortComparator);
      }
      return gadgets;
   }

   public void saveGadget(Gadget gadget) throws Exception
   {
      if (gadget == null)
      {
         throw new NullPointerException();
      }

      //
      GadgetRegistry registry = getRegistry();
      GadgetDefinition def = registry.getGadget(gadget.getName());

      //
      if (def == null)
      {
         throw new IllegalArgumentException("No such gadget " + gadget.getName());
      }

      //
      saveGadget(def, gadget);
   }

   public void removeGadget(String name) throws Exception
   {
      if (name == null)
      {
         throw new NullPointerException();
      }

      //
      GadgetRegistry registry = getRegistry();
      GadgetDefinition def = registry.getGadget(name);

      //
      if (def == null)
      {
         throw new IllegalArgumentException("No such gadget " + name);
      }

      //
      registry.removeGadget(name);
   }

   private void saveGadget(GadgetDefinition def, Gadget gadget)
   {
      def.setDescription(gadget.getDescription());
      def.setReferenceURL(gadget.getReferenceUrl());
      def.setTitle(gadget.getTitle());
      def.setThumbnail(gadget.getThumbnail());
   }

   private Gadget loadGadget(GadgetDefinition def)
   {
      GadgetData data = def.getData();

      //
      String url;
      if (data instanceof LocalGadgetData)
      {
         LocalGadgetData localData = (LocalGadgetData)data;
         url = "jcr/repository/portal-system/gadgets/" + def.getName() + "/data/resources/" + localData.getFileName();
      }
      else
      {
         RemoteGadgetData remoteData = (RemoteGadgetData)data;
         url = remoteData.getURL();
      }

      //
      Gadget gadget = new Gadget();
      gadget.setName(def.getName());
      gadget.setDescription(def.getDescription());
      gadget.setLocal(def.isLocal());
      gadget.setTitle(def.getTitle());
      gadget.setReferenceUrl(def.getReferenceURL());
      gadget.setThumbnail(def.getThumbnail());
      gadget.setUrl(url);
      return gadget;
   }

   public boolean isGadgetDeveloper(String username)
   {
      return true;
   }

   public String getCountry()
   {
      return country ;
   }

   public String getLanguage()
   {
      return language ;
   }

   public String getModuleId()
   {
      return moduleId;
   }

   public String getHostName()
   {
      return hostName ;
   }
}
