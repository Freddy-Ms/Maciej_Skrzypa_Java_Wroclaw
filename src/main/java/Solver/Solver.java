package Solver;
import java.util.List;
import java.util.Map;

public interface Solver<T> {
    Map<String,Float> solve(List<T> input);
}
