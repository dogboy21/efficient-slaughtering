/*
 * efficient-slaughtering
 * Copyright (C) 2018 Dogboy21
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ml.dogboy.efficientslaughtering.api;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.EntityLiving;

import java.util.*;

public class SlaughteringRegistry {

    private static final Map<Class<? extends EntityLiving>, String> nameCache = new HashMap<>();
    private static String getName(Class<? extends EntityLiving> entity) {
        if (!SlaughteringRegistry.nameCache.containsKey(entity)) {
            SlaughteringRegistry.nameCache.put(entity, entity.getCanonicalName());
        }

        return SlaughteringRegistry.nameCache.get(entity);
    }

    private static final Set<String> blacklist = new HashSet<>();
    private static final Map<String, Set<String>> preciseStrippedTags = new HashMap<>();
    private static final Set<String> globalPreciseStrippedTags = new HashSet<>();

    public static void addToBlacklist(Class<? extends EntityLiving> entity) {
        SlaughteringRegistry.blacklist.add(SlaughteringRegistry.getName(entity));
    }

    public static void stripNbtTag(Class<? extends EntityLiving> entity, String tagMatcher) {
        String key = SlaughteringRegistry.getName(entity);

        if (!SlaughteringRegistry.preciseStrippedTags.containsKey(key)) {
            SlaughteringRegistry.preciseStrippedTags.put(key, new HashSet<>());
        }

        SlaughteringRegistry.preciseStrippedTags.get(key).add(tagMatcher);
    }

    public static void stripNbtTag(String tagMatcher) {
        SlaughteringRegistry.globalPreciseStrippedTags.add(tagMatcher);
    }

    public static boolean isBlacklisted(Class<? extends EntityLiving> entity) {
        return SlaughteringRegistry.blacklist.contains(SlaughteringRegistry.getName(entity));
    }

    public static boolean isBlacklisted(EntityLiving entityLiving) {
        return SlaughteringRegistry.isBlacklisted(entityLiving.getClass());
    }

    public static Set<String> getStrippedTags(Class<? extends EntityLiving> entity) {
        return ImmutableSet.<String>builder()
                .addAll(SlaughteringRegistry.preciseStrippedTags.getOrDefault(SlaughteringRegistry.getName(entity),
                        Collections.emptySet()))
                .addAll(SlaughteringRegistry.globalPreciseStrippedTags)
                .build();
    }

}
