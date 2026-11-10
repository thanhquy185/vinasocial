package vn.tuhoc.RelationshipService.services.interfaces;

import vn.tuhoc.RelationshipService.dtos.events.PostPublishedEventDTO;
import vn.tuhoc.RelationshipService.dtos.events.PostUpdatedEventDTO;

public interface ConsumerService {
    void onPostPublished(PostPublishedEventDTO event);

    void onPostUpdated(PostUpdatedEventDTO event);
}
