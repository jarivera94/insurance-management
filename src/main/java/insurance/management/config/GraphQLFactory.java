package insurance.management.config;

import graphql.GraphQL;
import graphql.GraphQL.Builder;
import graphql.schema.GraphQLSchema;
import insurance.management.service.graphql.GraphQLService;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.inject.qualifiers.Qualifiers;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Factory
@Slf4j
public class GraphQLFactory {

  @Inject
  protected BeanContext beanContext;

  @Bean
  @Singleton
  public GraphQL graphQL() {

    GraphQLSchemaGenerator schemaGenerator = new GraphQLSchemaGenerator();

    Collection graphQLServices = beanContext.getBeansOfType(Object.class, Qualifiers.byStereotype(
        GraphQLService.class));

    if (graphQLServices.isEmpty()) {
      log.debug("No GraphQL services found, returning empty schema");
      return new Builder(GraphQLSchema.newSchema().build())
          .build();
    } else {
      for (Object graphQLService: graphQLServices) {
        Class graphQLServiceClass = graphQLService.getClass();
        if (graphQLServiceClass.getSimpleName().contains("$Intercepted"))
          graphQLServiceClass = graphQLServiceClass.getSuperclass();

        log.debug("Registering GraphQL service: {}", graphQLServiceClass.getSimpleName());
        schemaGenerator.withOperationsFromSingleton(graphQLService, graphQLServiceClass);
      }
    }

    return new GraphQL.Builder(schemaGenerator.generate()).build();
  }
}
