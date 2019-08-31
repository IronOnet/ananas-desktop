package org.ananas.runner.paginator.files;

import org.ananas.runner.core.errors.ErrorHandler;
import org.ananas.runner.core.paginate.AbstractPaginator;
import org.apache.beam.sdk.schemas.Schema;

public abstract class AbstractFilePaginator extends AbstractPaginator {

  protected String url;
  protected ErrorHandler errors;

  public AbstractFilePaginator(String id, String url) {
    super(id, null);
    this.url = url;
    this.errors = new ErrorHandler();
    this.schema = null;
  }

  protected abstract Schema autodetect(Integer pageSize);
}
