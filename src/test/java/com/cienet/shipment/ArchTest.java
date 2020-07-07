package com.cienet.shipment;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {

        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.cienet.shipment");

        noClasses()
            .that()
                .resideInAnyPackage("com.cienet.shipment.service..")
            .or()
                .resideInAnyPackage("com.cienet.shipment.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..com.cienet.shipment.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses);
    }
}
