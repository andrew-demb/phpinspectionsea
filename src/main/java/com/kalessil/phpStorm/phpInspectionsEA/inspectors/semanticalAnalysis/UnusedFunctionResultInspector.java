package com.kalessil.phpStorm.phpInspectionsEA.inspectors.semanticalAnalysis;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.kalessil.phpStorm.phpInspectionsEA.EAUltimateApplicationComponent;
import com.kalessil.phpStorm.phpInspectionsEA.openApi.BasePhpElementVisitor;
import com.kalessil.phpStorm.phpInspectionsEA.openApi.BasePhpInspection;
import com.kalessil.phpStorm.phpInspectionsEA.options.OptionsComponent;
import com.kalessil.phpStorm.phpInspectionsEA.utils.NamedElementUtil;
import com.kalessil.phpStorm.phpInspectionsEA.utils.OpenapiResolveUtil;
import com.kalessil.phpStorm.phpInspectionsEA.utils.OpenapiTypesUtil;
import com.kalessil.phpStorm.phpInspectionsEA.utils.Types;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * This file is part of the Php Inspections (EA Extended) package.
 *
 * (c) Vladimir Reznichenko <kalessil@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

public class UnusedFunctionResultInspector extends BasePhpInspection {
    // Inspection options.
    public boolean REPORT_ONLY_SCALARS = false;

    private static final String message = "Function result is not used.";

    private static final Set<String> ignoredFunctions = new HashSet<>();
    static {
        ignoredFunctions.add("end");
        ignoredFunctions.add("next");
        ignoredFunctions.add("reset");
        ignoredFunctions.add("array_shift");
        ignoredFunctions.add("array_pop");
        ignoredFunctions.add("array_splice");

        ignoredFunctions.add("print_r");
        ignoredFunctions.add("exec");
        ignoredFunctions.add("system");
        ignoredFunctions.add("session_id");
        ignoredFunctions.add("session_name");
        ignoredFunctions.add("call_user_func_array");
        ignoredFunctions.add("call_user_func");

        ignoredFunctions.add("ini_set");
        ignoredFunctions.add("set_include_path");
        ignoredFunctions.add("set_error_handler");
        ignoredFunctions.add("set_exception_handler");
        ignoredFunctions.add("setlocale");
        ignoredFunctions.add("mb_internal_encoding");
    }

    @NotNull
    public String getShortName() {
        return "UnusedFunctionResultInspection";
    }

    @Override
    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new BasePhpElementVisitor() {
            @Override
            public void visitPhpMethodReference(@NotNull MethodReference reference) {
                if (!EAUltimateApplicationComponent.areFeaturesEnabled()) { return; }
                if (this.isContainingFileSkipped(reference))              { return; }

                final String methodName = reference.getName();
                if (methodName != null && !methodName.equals("__construct")) {
                    this.analyze(reference);
                }
            }

            @Override
            public void visitPhpFunctionCall(@NotNull FunctionReference reference) {
                if (!EAUltimateApplicationComponent.areFeaturesEnabled()) { return; }
                if (this.isContainingFileSkipped(reference))              { return; }

                this.analyze(reference);
            }

            private void analyze(@NotNull FunctionReference reference) {
                final boolean isTargetContext = OpenapiTypesUtil.isStatementImpl(reference.getParent());
                if (isTargetContext && !ignoredFunctions.contains(reference.getName())) {
                    final PhpType resolved = OpenapiResolveUtil.resolveType(reference, reference.getProject());
                    if (resolved != null) {
                        final Set<String> types = resolved.filterUnknown().getTypes().stream()
                                .map(Types::getType)
                                .collect(Collectors.toSet());
                        types.remove(Types.strBoolean); /* APIs returning false on failures */
                        types.remove(Types.strInteger); /* APIs returning c-alike result codes */
                        types.remove(Types.strVoid);
                        if (!types.isEmpty()) {
                            final boolean skip      = REPORT_ONLY_SCALARS && types.removeIf(t -> t.startsWith("\\")) && types.isEmpty();
                            final PsiElement target = NamedElementUtil.getNameIdentifier(reference);
                            if (target != null && !skip) {
                                holder.registerProblem(target, message);
                            }
                        }
                        types.clear();
                    }
                }
            }
        };
    }

    public JComponent createOptionsPanel() {
        return OptionsComponent.create(
            (component) -> component.addCheckbox("Report only unused scalar results", REPORT_ONLY_SCALARS, (isSelected) -> REPORT_ONLY_SCALARS = isSelected)
        );
    }
}
