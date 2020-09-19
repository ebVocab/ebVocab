package de.ebuchner.vocab;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JDependTest extends TestCase {

    public JDependTest(String name) {
        super(name);
    }

    public void testCycles() throws Exception {

        JDepend jdepend = new JDepend();

        try {
            System.out.printf("cwd: %s%n", new File(".").getCanonicalPath());
            jdepend.addDirectory("out/production/Core");
            jdepend.addDirectory("out/production/Desktop");
            jdepend.addDirectory("out/production/mobile");
        } catch (Exception e) {
            e.printStackTrace();
            jdepend.addDirectory("build/classes/main");
        }

        Collection packages = jdepend.analyze();
        assertAwtFree("de.ebuchner.vocab.config.", packages);
        assertAwtFree("de.ebuchner.vocab.model", packages);
        assertAwtFree("de.ebuchner.vocab.tools", packages);
        assertAwtFree("de.ebuchner.vocab.nui", packages);

        assertNoDependency("de.ebuchner.vocab.tools", "de.ebuchner.vocab.config", packages);
        assertNoDependency("de.ebuchner.vocab.tools", "de.ebuchner.vocab.model", packages);
        assertNoDependency("de.ebuchner.vocab.tools", "de.ebuchner.vocab.nui", packages);

        // todo assertNoDependency("de.ebuchner.vocab.config", "de.ebuchner.vocab.model", packages);
        assertNoDependency("de.ebuchner.vocab.config", "de.ebuchner.vocab.nui", packages);

        // todo assertNoDependency("de.ebuchner.vocab.model", "de.ebuchner.vocab.nui", packages);

        checkCycles(jdepend);
    }

    private void checkCycles(JDepend jdepend) {
        if (!jdepend.containsCycles())
            return;

        for (Object objPackage : jdepend.getPackages()) {
            JavaPackage jPackage = (JavaPackage) objPackage;
            if (jPackage.containsCycle()) {
                List<JavaPackage> packages = new ArrayList<>();
                jPackage.collectCycle(packages);
                System.out.println("*** Package " + jPackage.getName() + " contains cycles:");
                for (JavaPackage depPackage : packages) {
                    System.out.println("  - " + depPackage.getName());
                }
            }
        }

    }

    private void assertAwtFree(String packageName, Collection packages) {
        assertNoDependency(packageName, "java.awt", packages);
        assertNoDependency(packageName, "javax.swing", packages);
    }

    private void assertNoDependency(String packageName, String otherPackageName, Collection packages) {
        assertFalse(
                packageName + " should not depend on " + otherPackageName,
                dependsUpon(packageName, otherPackageName, packages));
    }

    private boolean dependsUpon(String me, String other, Collection packages) {
        for (Object p : packages) {
            if (!(p instanceof JavaPackage))
                continue;
            JavaPackage jp = (JavaPackage) p;

            if (jp.getName().equals(me) || jp.getName().startsWith(String.format("%s.", me))) {
                Collection outgoingDependencies = jp.getEfferents();
                for (Object op : outgoingDependencies) {
                    if (!(op instanceof JavaPackage))
                        continue;
                    JavaPackage ojp = (JavaPackage) op;
                    if (ojp.getName().startsWith(other)) {
                        System.out.println(jp.getName() + " depends on " + ojp.getName());
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
