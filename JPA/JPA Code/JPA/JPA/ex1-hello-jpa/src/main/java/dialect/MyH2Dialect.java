package dialect;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

public class MyH2Dialect implements FunctionContributor {
    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions
                .getFunctionRegistry()
                .registerNamed("group_concat", functionContributions.getTypeConfiguration()
                                                                         .getBasicTypeRegistry()
                                                                         .resolve(StandardBasicTypes.STRING));
    }
}
