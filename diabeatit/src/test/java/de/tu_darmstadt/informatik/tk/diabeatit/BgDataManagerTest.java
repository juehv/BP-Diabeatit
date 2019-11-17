package de.tu_darmstadt.informatik.tk.diabeatit;

import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.tu_darmstadt.informatik.tk.diabeatit.data.BGSinks.DummySink;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BGSources.DummySource;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class BgDataManagerTest {

    @Before
    public void prepare() {
        PowerMockito.mockStatic(Log.class);
    }

    @Test
    public void testReadingsBeingReceived() {
        // setup all dummies
        DummySource src = new DummySource();
        DummySink sink = new DummySink();
        BgDataManager mgr = new BgDataManager(src, sink, null);

        // fake receiving some data (can be null since DummySource does not interact with it)
        mgr.onReceive(null, null);

        // now the sink should have received some data

        Assert.assertNotEquals(null, sink.lastReading);
        Assert.assertEquals(src.dummyReading.rawValue, sink.lastReading.rawValue,  0.1);
        Assert.assertEquals(src.dummyReading.rawUnit, sink.lastReading.rawUnit);
        Assert.assertEquals(1, sink.getTotalReadingsReceived());
    }
}
