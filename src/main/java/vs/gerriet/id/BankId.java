package vs.gerriet.id;

import spark.Request;

/**
 * Bank id container.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class BankId extends Id<Integer> {

    /**
     * Creates a new bank id container.
     *
     * @param data
     *            Contained id.
     */
    public BankId(final Integer data) {
        super(data);
    }

    /**
     * Creates a new bank id container.
     *
     * @param request
     *            Request to load the id from.
     * @param param
     *            Parameter name containing the id.
     */
    public BankId(final Request request, final String param) {
        super(request, param);
    }

    @Override
    protected Integer fromUriSuffix(final String suffix) {
        return Integer.getInteger(suffix);
    }

    @Override
    protected String getUriPrefix() {
        return "/banks/";
    }
}