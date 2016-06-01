package vs.gerriet.api.bank;

import java.util.Collections;
import java.util.List;

import com.mashape.unirest.http.HttpResponse;

import vs.gerriet.api.LazyList;
import vs.gerriet.exception.ApiException;
import vs.gerriet.id.BankId;
import vs.gerriet.utils.CollectionUtils;

/**
 * <p>
 * Bank list API class.
 * </p>
 * <p>
 * This list is not modifiable. Except when calling {@link #refresh()}. This
 * list is thread save.
 * </p>
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 *
 */
public class BankList extends BankBase implements LazyList<Bank> {

    /**
     * List of banks, will be loaded within constructor. Cannot be modified.
     */
    private List<Bank> list;

    /**
     * Creates a new list of all banks known by the bank service.
     *
     * @throws ApiException
     *             If the list could not be loaded from the bank service.
     */
    public BankList() throws ApiException {
        this.refresh();
    }

    @Override
    public List<Bank> getInternalList() {
        return this.list;
    }

    @Override
    public void load() throws ApiException {
        if (this.list == null) {
            this.refresh();
        }
    }

    @Override
    public void refresh() throws ApiException {
        final HttpResponse<vs.gerriet.json.BankList> result = this.requestGetBankList();
        if (result == null || result.getStatus() != 200) {
            throw new ApiException("Failed to load bank list from service.");
        }
        // create Bank list from bank uri array
        final List<Bank> bankList = CollectionUtils.listMapParallel(result.getBody().banks, uri -> {
            final BankId id = new BankId(null);
            id.loadUri(uri);
            return new Bank(id);
        });
        // make list read only
        this.list = Collections.unmodifiableList(bankList);
    }
}
