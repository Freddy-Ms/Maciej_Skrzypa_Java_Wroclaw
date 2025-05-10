package Solver;
import java.util.List;
import java.util.Map;

public interface Solver<T,U> {
    Map<String,Float> solve(List<T> input, List<U> input2);
}
