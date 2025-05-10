package solver;
import java.util.List;
import java.util.Map;

public interface Solver<T,U> {
    Map<String,Float> solve(List<T> dataRepresentation, List<U> baseItem);
}
