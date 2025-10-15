// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

import com.azure.autorest.customization.ClassCustomization;
import com.azure.autorest.customization.Customization;
import com.azure.autorest.customization.LibraryCustomization;
import com.azure.autorest.customization.PackageCustomization;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.slf4j.Logger;

public class KeyVaultCustomization extends Customization {
    @Override
    public void customize(LibraryCustomization customization, Logger logger) {
        PackageCustomization fluentModelsPackage = customization.getPackage("com.azure.resourcemanager.keyvault.fluent.models");
        customizeResourceBaseClass(fluentModelsPackage.getClass("VaultInner"));
    }

    private static void customizeResourceBaseClass(ClassCustomization customization) {
        customization.customizeAst(ast -> {
            String resourceClassName = "com.azure.core.management.Resource";
            ast.getClassByName(customization.getClassName()).ifPresent(clazz -> {
                ast.getClassByName(resourceClassName).ifPresent(resourceClazz -> {
                    ast.addImport(resourceClassName);
                    clazz.getExtendedTypes().clear();
                    clazz.addExtendedType(new ClassOrInterfaceType(null, "Resource"));
                });
            });
        });
    }
}
