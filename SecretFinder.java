import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SecretFinder {

    // Method to convert a number in any base to decimal using BigInteger
    public static BigInteger convertToDecimal(String value, int base) {
        return new BigInteger(value, base);
    }

    // Parse the JSON data to extract points and the k value
    public static List<BigInteger[]> parseTestCase(JSONObject testCase) {
        List<BigInteger[]> points = new ArrayList<>();
        JSONObject keys = testCase.getJSONObject("keys");
        int k = keys.getInt("k");  // Number of points needed for interpolation

        for (String key : testCase.keySet()) {
            if (!key.equals("keys")) {
                BigInteger x = new BigInteger(key);  // Use key as x
                JSONObject point = testCase.getJSONObject(key);
                int base = point.getInt("base");
                String value = point.getString("value");
                BigInteger y = convertToDecimal(value, base);
                points.add(new BigInteger[]{x, y});
            }
        }

        // Return points up to the first k elements
        return points.subList(0, k);
    }

    // Lagrange interpolation to find the constant term
    public static BigInteger lagrangeInterpolation(List<BigInteger[]> points) {
        BigInteger constantTerm = BigInteger.ZERO;
        int n = points.size();

        for (int i = 0; i < n; i++) {
            BigInteger xi = points.get(i)[0];
            BigInteger yi = points.get(i)[1];

            BigInteger term = yi;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    BigInteger xj = points.get(j)[0];
                    BigInteger numerator = xj.negate();
                    BigInteger denominator = xi.subtract(xj);

                    // Multiply term by (0 - xj) / (xi - xj)
                    term = term.multiply(numerator).divide(denominator);
                }
            }
            constantTerm = constantTerm.add(term);
        }
        return constantTerm;
    }

    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("input.json")) {
            JSONTokener tokener = new JSONTokener(fis);
            JSONObject testCase = new JSONObject(tokener);

            // Parse test case and compute the secret constant term
            List<BigInteger[]> points = parseTestCase(testCase);
            BigInteger secret = lagrangeInterpolation(points);

            System.out.println("The constant term (secret) is: " + secret);
        } catch (IOException e) {
            System.err.println("Error reading the JSON file: " + e.getMessage());
        }
    }
}
