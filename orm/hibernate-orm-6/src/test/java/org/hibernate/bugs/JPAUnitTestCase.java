package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
	To set up for this test, provide Oracle connection info in persistence.xml and run the following in Oracle (tested in 19c)

 	 CREATE TABLE HHH_TEST (
 	                           ID NUMBER PRIMARY KEY,
 	                           FOO VARCHAR2(255)
 	 );
 	 /

 	 CREATE OR REPLACE EDITIONABLE TRIGGER hhh_test_set_defaults_trigger
 	     FOR INSERT OR UPDATE OR DELETE ON HHH_TEST
 	     COMPOUND TRIGGER

 	     TYPE id_t IS TABLE OF NUMBER;
 	     g_ids id_t := id_t();

 	     BEFORE EACH ROW IS
 	     BEGIN
 	         IF INSERTING THEN
 	             :NEW.foo := 'before each row value';
 	             g_ids.EXTEND;
 	             g_ids(g_ids.COUNT) := :NEW.id;
 	         END IF;
 	     END BEFORE EACH ROW;

 	     AFTER STATEMENT IS
 	     BEGIN
 	         FORALL i IN 1 .. g_ids.COUNT
 	             UPDATE hhh_test
 	             SET foo = 'after statement value'
 	             WHERE id = g_ids(i);
 	     END AFTER STATEMENT;
 	 END hhh_test_set_defaults_trigger;
 	 /
 	 ALTER TRIGGER hhh_test_set_defaults_trigger ENABLE
 	 /
 */
class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@BeforeEach
	void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "oraclePersistenceUnit" );

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.createQuery("delete from TestEntity").executeUpdate();
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@AfterEach
	void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	void hhhTest() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		TestEntity te = new TestEntity();
		te.setId(1);

		entityManager.persist(te);
		entityManager.getTransaction().commit();

		/**
		 * This line fails:
		 * Expected :after statement value
		 * Actual   :before each row value
		 */
		Assertions.assertEquals("after statement value", te.getFoo());

		entityManager.close();
	}
}
