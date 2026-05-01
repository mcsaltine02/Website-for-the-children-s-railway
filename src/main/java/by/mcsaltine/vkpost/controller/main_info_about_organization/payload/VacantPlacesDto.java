package by.mcsaltine.vkpost.controller.main_info_about_organization.payload;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class VacantPlacesDto{Integer vacantPlaces; Integer tpId;}

