package com.distribuida.servicios;

import com.distribuida.db.Persona;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class ServicioPersonaImpl implements ServicioPersona {
    @Inject
    EntityManager em;

    @Override
    public List<Persona> findAll() {
        return em.createQuery("select o from Persona o")
                .getResultList();
    }

    public Persona findById(Integer id) {
        return em.find(Persona.class, id);
    }

    public void insert(Persona p) {
        var tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(p);
            tx.commit();
        }
        catch(Exception ex) {
            tx.rollback();
        }
    }

    @Override
    public void delete(Integer id) {
        var tx = em.getTransaction();

        Persona p = findById(id);

        try {
                    tx.begin();
                    em.remove(p);
                    tx.commit();
        } catch (Exception ex) {
            tx.rollback();       
        }
        
    }

    @Override
    public void update(Persona p) {
       var tx = em.getTransaction();

       Persona pUpdate = findById(p.getId());

       pUpdate.setNombre(p.getNombre());
       pUpdate.setDireccion(p.getDireccion());
       pUpdate.setEdad(p.getEdad());
       pUpdate.setId(p.getId());

        try {
            tx.begin();
            em.merge(pUpdate);
            tx.commit();
        } catch (Exception ex) {
            tx.rollback();       
        }
    }
}
