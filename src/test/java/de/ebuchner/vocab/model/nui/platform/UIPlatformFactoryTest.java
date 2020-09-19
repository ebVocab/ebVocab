package de.ebuchner.vocab.model.nui.platform;

import junit.framework.TestCase;

public class UIPlatformFactoryTest extends TestCase {

    public void testFactory() {
        UIPlatform platform = UIPlatformFactory.getUIPlatform();
        assertNotNull(platform);
        assertEquals(UIPlatformType.FX, platform.getType());
        assertSame(platform, UIPlatformFactory.getUIPlatform());
    }

}
