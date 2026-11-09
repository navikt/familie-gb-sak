package no.nav.gravferd.sak.arkitektur

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.properties.HasName.Predicates.name
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.Test
import org.springframework.stereotype.Service

class ArkitekturTest {
    @Test
    fun `avhengigheter peker mot kjernen`() {
        layeredArchitecture()
            .consideringOnlyDependenciesInAnyPackage("$ROT..")
            .withOptionalLayers(true)
            .layer("Oppstart")
            .definedBy(name("$ROT.Application").or(name("$ROT.ApplicationKt")))
            .layer("Kjerne")
            .definedBy("$ROT.kjerne..")
            .layer("Inn")
            .definedBy("$ROT.inn..")
            .layer("Ut")
            .definedBy("$ROT.ut..")
            .layer("Common")
            .definedBy("$ROT.common..")
            .layer("Config")
            .definedBy("$ROT.config..")
            .whereLayer("Oppstart")
            .mayNotBeAccessedByAnyLayer()
            .whereLayer("Config")
            .mayNotBeAccessedByAnyLayer()
            .whereLayer("Inn")
            .mayOnlyBeAccessedByLayers("Config")
            .whereLayer("Ut")
            .mayOnlyBeAccessedByLayers("Config")
            .whereLayer("Kjerne")
            .mayOnlyBeAccessedByLayers("Inn", "Ut", "Config")
            .check(klasser)
    }

    @Test
    fun `inn og ut inneholder ikke servicer`() {
        noClasses()
            .that()
            .resideInAnyPackage("$ROT.inn..", "$ROT.ut..")
            .should()
            .beAnnotatedWith(Service::class.java)
            .check(klasser)
    }

    @Test
    fun `all produksjonskode ligger i et kjent lag`() {
        classes()
            .that()
            .doNotHaveFullyQualifiedName("$ROT.Application")
            .and()
            .doNotHaveFullyQualifiedName("$ROT.ApplicationKt")
            .should()
            .resideInAnyPackage(
                "$ROT.kjerne..",
                "$ROT.inn..",
                "$ROT.ut..",
                "$ROT.common..",
                "$ROT.config..",
            ).check(klasser)
    }

    private companion object {
        const val ROT = "no.nav.gravferd.sak"

        val klasser: JavaClasses by lazy {
            ClassFileImporter()
                .withImportOption(ImportOption.DoNotIncludeTests())
                .importPackages(ROT)
        }
    }
}
