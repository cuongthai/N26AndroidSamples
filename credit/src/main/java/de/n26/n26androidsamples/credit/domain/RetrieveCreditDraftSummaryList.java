package de.n26.n26androidsamples.credit.domain;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import de.n26.n26androidsamples.base.common.rx.UnwrapOptionTransformer;
import de.n26.n26androidsamples.base.domain.ReactiveInteractor.RetrieveInteractor;
import de.n26.n26androidsamples.credit.data.CreditDraft;
import de.n26.n26androidsamples.credit.data.CreditRepository;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import polanski.option.Option;

public class RetrieveCreditDraftSummaryList implements RetrieveInteractor<Void, List<CreditDraft>> {

    @NonNull
    private final CreditRepository creditRepository;

    @Inject
    RetrieveCreditDraftSummaryList(@NonNull final CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }

    /**
     Emits updates of the credit draft summaries.
     It will fetch in the case the repository has no local data stored.

     @param params no params required, pass {@link Option#NONE}
     */
    @NonNull
    @Override
    public Flowable<List<CreditDraft>> getBehaviorStream(@NonNull final Option<Void> params) {
        return creditRepository.getAllCreditDrafts()
                               .flatMap(draftsOption -> fetchIfRepositoryIsEmpty(draftsOption).andThen(Flowable.just(draftsOption)))
                               .compose(new UnwrapOptionTransformer<>());
    }

    @NonNull
    private Completable fetchIfRepositoryIsEmpty(@NonNull final Option<List<CreditDraft>> drafts) {
        return drafts.isNone()
                ? creditRepository.fetchCreditDrafts()
                : Completable.complete();
    }

}
