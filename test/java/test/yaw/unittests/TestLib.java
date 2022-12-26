package test.yaw.unittests;

public class TestLib {
    public static class TestError extends RuntimeException {
        public TestError(String msg) {
            super("Test Error: " + msg);
        }
    }
}
