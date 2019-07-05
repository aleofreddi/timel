package net.vleo.timel.impl.function.version;

/*-
 * #%L
 * TimEL core
 * %%
 * Copyright (C) 2015 - 2019 Andrea Leofreddi
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import lombok.val;
import net.vleo.timel.annotation.FunctionPrototype;
import net.vleo.timel.annotation.Returns;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.function.Function;
import net.vleo.timel.impl.downscaler.Downscaler;
import net.vleo.timel.impl.target.Evaluable;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.iterator.SingletonUpscalableTimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.Sample;
import net.vleo.timel.type.StringType;

import java.io.IOException;
import java.util.Properties;

/**
 * A function to retrieve TimEL version.
 *
 * @author Andrea Leofreddi
 */
@FunctionPrototype(
        name = "version",
        returns = @Returns(type = StringType.class),
        parameters = {}
)
public class VersionFunction implements Function<String> {
    @Override
    public UpscalableIterator<String> evaluate(Interval interval, ExecutorContext context, Upscaler<String> upscaler, Downscaler<String> downscaler, Evaluable<?>[] arguments) {
        return new SingletonUpscalableTimeIterator<>(
                upscaler,
                Sample.of(interval, getVersion())
        );
    }

    private String getVersion() {
        String version = "?", commitId = "?";

        try(val is = getClass().getClassLoader().getResourceAsStream("git.properties")) {
            if(is != null) {
                Properties properties = new Properties();
                properties.load(is);

                version = properties.getProperty("git.build.version");
                commitId = properties.getProperty("git.commit.id");
            }
        } catch(IOException e) {
            // Do nothing
        }

        return version + " (" + commitId + ")";
    }
}
