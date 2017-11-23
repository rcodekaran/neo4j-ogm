package org.neo4j.ogm.persistence.examples.movies;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.neo4j.ogm.cypher.query.Pagination;
import org.neo4j.ogm.domain.cineasts.annotated.Actor;
import org.neo4j.ogm.domain.cineasts.annotated.Movie;
import org.neo4j.ogm.domain.cineasts.annotated.Rating;
import org.neo4j.ogm.domain.cineasts.annotated.Role;
import org.neo4j.ogm.domain.cineasts.annotated.User;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.testutil.MultiDriverTestClass;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Frantisek Hartman
 */
public class MoviesIntegrationTest extends MultiDriverTestClass {

    private Session session;

    @BeforeClass
    public static void oneTimeSetUp() {
        sessionFactory = new SessionFactory(driver, "org.neo4j.ogm.domain.cineasts.annotated");
    }

    @Before
    public void init() throws IOException {
        session = sessionFactory.openSession();
        session.purgeDatabase();
    }

    @Test
    public void shouldLoadRelatedNodesForRelationshipEntity() throws Exception {

        User frantisek = new User();
        frantisek.setLogin("Frantisek");

        Movie matrix = new Movie("Matrix", 1999);
        Actor keanu = new Actor("Keanu Reaves");
        Role neoRole = new Role(matrix, keanu, "Neo");
        matrix.setRoles(newHashSet(neoRole));
        keanu.setRoles(newHashSet(neoRole));


        Rating rating = new Rating();
        rating.setMovie(matrix);
        rating.setUser(frantisek);
        matrix.setRatings(newHashSet(rating));
        frantisek.setRatings(newHashSet(rating));

        session.save(frantisek);

        session.clear();

        Collection<Role> roles = session.loadAll(Role.class, new Pagination(0, 1));

        Role role = roles.iterator().next();
        Set<Rating> ratings = role.getMovie().getRatings();
        assertThat(ratings).hasSize(1);

        User user = ratings.iterator().next().getUser();
        assertThat(user.getLogin()).isEqualTo("Frantisek");
    }
}
