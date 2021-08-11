package com.serverless.LocationServices;

public class GeoHash {

	/**
	 *
	 *
	 * This class will provide an implementation of GeoHashes:
	 *
	 * https://www.mapzen.com/blog/geohashes-and-you/
	 * https://en.wikipedia.org/wiki/Geohash
	 *
	 *
	 * GeoHash Spatial Precision:
	 *
	 * https://releases.dataone.org/online/api-documentation-v2.0.1/design/geohash.html
	 */

	public static final double[] LATITUDE_RANGE = { -90, 90 };
	public static final double[] LONGITUDE_RANGE = { -180, 180 };
	
	private static GeoHash instance = null;
    public static GeoHash getInstance() {
        if (instance == null) {
            instance = new GeoHash();
        }
        return instance;
    }
    
    

	public static boolean[] geohash1D(double valueToHash, double[] valueRange, int bitsOfPrecision) {
		// The bits of precision is the number of bits that should be in your output
		// hash.
		//
		// We are approximating "bits" with a boolean array to make things simpler.
		boolean[] boolArray = new boolean[bitsOfPrecision];

		double[] bounds = { valueRange[0], valueRange[1] };
		double midpoint;

		for (int i = 0; i < bitsOfPrecision; i++) {

			midpoint = (bounds[0] + bounds[1]) / 2;

			if (valueToHash >= midpoint) {
				boolArray[i] = true;
				bounds[0] = midpoint;
			} else {
				boolArray[i] = false;
				bounds[1] = midpoint;
			}

		}

		// System.out.println("hash to string " + toHashString(boolArray));
		return boolArray;
	}

	public static boolean[] geohash2D(double v1, double[] v1range, double v2, double[] v2range, int bitsOfPrecision) {

		// Separately compute indvidual 1D geohashes for v1 and v2 and then interleave
		// them together
		// into a final combined geohash.
		//
		// The resulting geohash should have the number of bits specified by
		// bitsOfPrecision.
		//
		boolean[] latHash = geohash1D(v1, v1range, bitsOfPrecision);
		boolean[] longHash = geohash1D(v2, v2range, bitsOfPrecision);
		boolean[] combinedHash = new boolean[bitsOfPrecision];

		for (int i = 0; i < bitsOfPrecision; i++) {

			if (i % 2 == 0)
				combinedHash[i] = latHash[i / 2];

			else
				combinedHash[i] = longHash[i / 2];

		}

		return combinedHash;

	}

	public static boolean[] geohash(double lat, double lon, int bitsOfPrecision) {
		return geohash2D(lat, LATITUDE_RANGE, lon, LONGITUDE_RANGE, bitsOfPrecision);
	}

	// This is a helper method that will make printing out
	// geohashes easier
	public static String toHashString(boolean[] geohash) {
		String hashString = "";
		for (boolean b : geohash) {
			hashString += (b ? "1" : "0");
		}
		return hashString;
	}

	// This is a convenience method to make it easy to get a string of 1s and 0s for
	// a
	// geohash
	public static String geohashString(double valueToHash, double[] valueRange, int bitsOfPrecision) {
		return toHashString(geohash1D(valueToHash, valueRange, bitsOfPrecision));
	}

}