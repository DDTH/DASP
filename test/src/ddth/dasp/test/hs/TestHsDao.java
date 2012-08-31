package ddth.dasp.test.hs;

import java.util.Date;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

import ddth.dasp.handlersocket.hsc.IHscFactory;
import ddth.dasp.handlersocket.hsc.hs4j.Hs4jHscFactory;
import ddth.dasp.test.hs.bo.IMyDao;
import ddth.dasp.test.hs.bo.MyDao;

public class TestHsDao {

	private static IHscFactory initHscFactory() {
		Hs4jHscFactory hscf = new Hs4jHscFactory();
		hscf.init();
		return hscf;
	}

	private static void destroyHscFactory(IHscFactory hscf) {
		((Hs4jHscFactory) hscf).destroy();
	}

	private static IMyDao initDao(IHscFactory hscf) {
		MyDao bom = new MyDao();
		bom.setHscFactory(hscf);
		bom.setDbHost("10.60.7.229");
		bom.setDbPort(IHscFactory.PORT_READWRITE);
		bom.setQueryConfigLocation("/ddth/dasp/test/hs/query_config.xml");
		bom.init();
		return bom;
	}

	private static void destroyDao(IMyDao dao) {
		((MyDao) dao).destroy();
	}

	public static void main(String[] args) throws Exception {
		// byte[] data = new byte[0];
		// String str = new String(data, "UTF-8");
		// System.exit(0);

		IHscFactory hscf = initHscFactory();
		IMyDao dao = initDao(hscf);
		Random random = new Random(System.currentTimeMillis());
		long time1 = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			int id = random.nextInt();
			String cString = RandomStringUtils.randomAlphanumeric(random
					.nextInt(32) + 1);
			int cInt = random.nextInt();
			double cReal = random.nextDouble();
			Date cDate = new Date(random.nextLong() % 1500000000L);
			byte[] cBinary = RandomStringUtils.random(random.nextInt(32) + 1)
					.getBytes();
			dao.createRow(new Object[] { id, cString, cInt, cReal, cDate,
					cBinary });
			System.out.println(id + "|" + cString + "|" + cInt + "|" + cReal
					+ "|" + cDate);
		}
		long time2 = System.currentTimeMillis();
		System.out.println(time2 - time1);

		destroyDao(dao);
		destroyHscFactory(hscf);
	}
}
