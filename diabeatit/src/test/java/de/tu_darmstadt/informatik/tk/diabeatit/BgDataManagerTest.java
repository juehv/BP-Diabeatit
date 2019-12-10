package de.tu_darmstadt.informatik.tk.diabeatit;

import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.tu_darmstadt.informatik.tk.diabeatit.data.BgReading;
import de.tu_darmstadt.informatik.tk.diabeatit.data.DummySink;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BGSources.DummyBgSource;
import de.tu_darmstadt.informatik.tk.diabeatit.data.DataManager;

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
        DummyBgSource src = new DummyBgSource();
        DummySink<BgReading> sink = new DummySink<>();
        DataManager<BgReading> mgr = new DataManager<BgReading>(src, sink, null);

        // fake receiving some data (can be null since DummyBgSource does not interact with it)
        mgr.onReceive(null, null);

        // now the sink should have received some data

        Assert.assertNotEquals(null, sink.lastReading);
        Assert.assertEquals(src.dummyReading.rawValue, sink.lastReading.rawValue,  0.1);
        Assert.assertEquals(src.dummyReading.rawUnit, sink.lastReading.rawUnit);
        Assert.assertEquals(1, sink.getTotalReadingsReceived());
    }
}