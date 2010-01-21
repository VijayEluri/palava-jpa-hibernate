/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.cosmocode.palava.jobs.hib;

import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

import de.cosmocode.palava.Job;
import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.protocol.ConnectionLostException;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.server.Server;
import de.cosmocode.palava.services.persistence.ClosableSession;
import de.cosmocode.palava.services.persistence.HibernateService;

public abstract class HibJob implements Job {
    
    public static final String CADDY_HIBSESSION = "HibSession";
    
    @Override
    public final void process(Call request, Response response, de.cosmocode.palava.core.session.HttpSession s, Server server,
            Map<String, Object> caddy) throws ConnectionLostException, Exception {

        Session session = (Session) caddy.get(CADDY_HIBSESSION);
        process(request, response, s, server, caddy, session);

    }
    
    public static org.hibernate.Session createHibSession(Server server, Map<String, Object> caddy)  {
        
        HibernateService hib = server.getServiceManager().lookup(HibernateService.class);
        Session session = hib.getSessionFactory().openSession();
        caddy.put(CADDY_HIBSESSION, new ClosableSession(session));
        
        return session;
    }
    
    public final void flush (Session session) throws Exception {
        Transaction tx = session.beginTransaction();
        try {
            session.flush();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }


    public abstract void process(Call request, Response response, de.cosmocode.palava.core.session.HttpSession s, Server server,
        Map<String, Object> caddy, Session session) throws Exception;

}